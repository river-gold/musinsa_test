package musinsa.test.domain.point.config;

import lombok.Builder;
import musinsa.test.entity.point.PointConfigEntity;

import java.math.BigDecimal;

@Builder
public record PointConfig(BigDecimal maxEarnAmount) {
    public static PointConfig of(PointConfigEntity entity) {
        return PointConfig.builder()
                .maxEarnAmount(entity.getMaxEarnAmount())
                .build();
    }
}
