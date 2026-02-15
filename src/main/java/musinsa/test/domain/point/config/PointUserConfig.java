package musinsa.test.domain.point.config;

import lombok.Builder;
import musinsa.test.entity.point.PointUserConfigEntity;

import java.math.BigDecimal;

@Builder
public record PointUserConfig(Long userId, BigDecimal maxBalance) {
    public static PointUserConfig of(PointUserConfigEntity entity) {
        return PointUserConfig.builder()
                .userId(entity.getUserId())
                .maxBalance(entity.getMaxBalance())
                .build();
    }
}
