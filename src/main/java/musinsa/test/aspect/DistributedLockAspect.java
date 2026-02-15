package musinsa.test.aspect;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // @Transactional보다 먼저 실행하기 위해 최우선으로 변경
@RequiredArgsConstructor
public class DistributedLockAspect {
    private final RedissonClient redissonClient;

    @Transactional(value = Transactional.TxType.NEVER) // 락 획득 전 트랜잭션 실행 금지
    @Around("@annotation(musinsa.test.aspect.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock distributedLock = signature.getMethod().getAnnotation(DistributedLock.class);

        // 1. SpEL을 이용한 락 키 생성
        Object key = SpelParser.getDynamicValue(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                distributedLock.key()
        );

        if (key == null) {
            throw new IllegalArgumentException("락 키가 존재하지 않습니다.");
        }

        log.info("획득 키 : {}", key);

        RLock lock = redissonClient.getLock(key.toString());

        try {
            // 2. 락 획득 시도
            boolean available = lock.tryLock(
                    distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    TimeUnit.SECONDS
            );

            if (!available) {
                throw new IllegalStateException("락 회득 실패 하였습니다.");
            }

            log.info("락 획득 성공: {}", key);
            // 3. 실제 비즈니스 로직
            return joinPoint.proceed();

        } finally {
            // 4. 락 해제
            try {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.info("락 해제 완료: {}", key);
                }
            } catch (IllegalMonitorStateException e) {
                log.info("이미 만료된 락입니다.");
            }
        }
    }
}
