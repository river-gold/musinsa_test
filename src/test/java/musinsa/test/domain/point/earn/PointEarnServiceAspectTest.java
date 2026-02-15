package musinsa.test.domain.point.earn;

import musinsa.test.domain.point.PointService;
import musinsa.test.domain.point.transaction.PointTransactionSummaryService;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class PointEarnServiceAspectTest {
    @MockitoBean
    private PointEarnExecuteService pointEarnExecuteService;
    @MockitoBean
    private PointService pointService;
    @MockitoBean
    private PointTransactionSummaryService pointTransactionSummaryService;
    @MockitoBean
    private RedissonClient redissonClient;

    @Autowired
    private PointEarnService pointEarnService;

    @Test
    @DisplayName("분산락 테스트")
    void earnLockTest() {
        PointEarnCommand command = Instancio.create(PointEarnCommand.class);
        String lockKey = "user:point:%d".formatted(command.getUserId());

        given(redissonClient.getLock(lockKey)).willReturn(Mockito.mock());

        try {
            pointEarnService.earn(command);
        } catch (Exception e) {

        }
        verify(redissonClient, times(1)).getLock(lockKey);
    }

    @Test
    @DisplayName("분산락 테스트")
    void cancelLockTest() {
        PointEarnCancelCommand command = Instancio.create(PointEarnCancelCommand.class);
        String lockKey = "user:point:%d".formatted(command.userId());

        given(redissonClient.getLock(lockKey)).willReturn(Mockito.mock());

        try {
            pointEarnService.cancel(command);
        } catch (Exception e) {

        }
        verify(redissonClient, times(1)).getLock(lockKey);
    }
}
