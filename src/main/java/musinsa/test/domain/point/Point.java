package musinsa.test.domain.point;

import lombok.Builder;
import lombok.With;
import musinsa.test.domain.point.code.PointEarnType;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.entity.point.PointEntity;
import musinsa.test.utils.BigDecimalUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Builder
public record Point(Long id, String pointKey, Long userId, BigDecimal earnedAmount, @With BigDecimal balance,
                    LocalDate expireDate, @With PointStatus status, PointEarnType earnType, Optional<String> issuerId) {
    static public Point of(PointEntity entity) {
        return Point.builder()
                .id(entity.getId())
                .pointKey(entity.getPointKey())
                .userId(entity.getUserId())
                .earnedAmount(entity.getEarnedAmount())
                .balance(entity.getBalance())
                .expireDate(entity.getExpireDate())
                .status(entity.getStatus())
                .earnType(entity.getEarnType())
                .issuerId(Optional.ofNullable(entity.getIssuerId()))
                .build();
    }

    static public BigDecimal sumBalance(List<Point> points) {
        return BigDecimalUtils.sum(points, Point::balance);
    }

    @Override
    public PointStatus status() {
        if (PointStatus.EARNED == status) {
            if (balance().compareTo(BigDecimal.ZERO) == 0) {
                return PointStatus.EXHAUSTED;
            } else if (LocalDate.now().isAfter(expireDate())) {
                return PointStatus.EXPIRED;
            }
        }
        return status;
    }

    public PointEntity toEntity() {
        return baseBuilder()
                .id(id)
                .pointKey(pointKey)
                .build();
    }

    public PointEntity createEntity(String pointKey) {
        return baseBuilder()
                .pointKey(pointKey)
                .build();
    }

    private PointEntity.PointEntityBuilder baseBuilder() {
        return PointEntity.builder()
                .pointKey(pointKey)
                .userId(userId)
                .earnedAmount(earnedAmount)
                .balance(balance)
                .expireDate(expireDate)
                .status(status)
                .earnType(earnType)
                .issuerId(issuerId.orElse(null));
    }

    public Point cancel() {
        return this.withBalance(BigDecimal.ZERO).withStatus(PointStatus.CANCEL);
    }
}
