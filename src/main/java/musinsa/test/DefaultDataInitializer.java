package musinsa.test;


import lombok.RequiredArgsConstructor;
import musinsa.test.entity.point.PointConfigEntity;
import musinsa.test.entity.point.repo.PointConfigRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DefaultDataInitializer {
    private final PointConfigRepository pointConfigRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 1회 적립 가능한 포인트
        pointConfigRepository.save(PointConfigEntity.builder().maxEarnAmount(BigDecimal.valueOf(100000)).build());
    }
}
