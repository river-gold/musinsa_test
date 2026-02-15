package musinsa.test.domain.point.earn;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.config.PointConfig;
import musinsa.test.domain.point.config.PointUserConfig;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class PointEarnValidator {
    private final PointConfig pointConfig;
    private final Optional<PointUserConfig> pointUserConfig;
    private final BigDecimal sumBalance;
    private final Optional<Point> point;

    public void validate(PointEarnCommand command) {
        command.getExpireDays().ifPresent(expireDays -> {
            if (expireDays >= 365 * 5) {
                throw new IllegalArgumentException("만료일은 5년 이상 요청할 수 없습니다.");
            }
        });

        BigDecimal amount = command.getAmount();
        BigDecimal minEarnAmount = BigDecimal.ZERO;
        if (minEarnAmount.compareTo(amount) >= 0) {
            throw new IllegalArgumentException("충전 포인트는 0보다 커야 합니다.");
        }

        BigDecimal maxEarnAmount = pointConfig.maxEarnAmount();
        if (maxEarnAmount.compareTo(amount) < 0) {
            throw new IllegalArgumentException("충전 포인트는 %s 이하야 합니다.".formatted(maxEarnAmount));
        }

        pointUserConfig.ifPresent(config -> {
            BigDecimal userMaxBalance = config.maxBalance();
            BigDecimal earnAfterBalance = command.getAmount().add(sumBalance);
            if (userMaxBalance.compareTo(earnAfterBalance) < 0) {
                throw new IllegalArgumentException("충전 후 잔액은 %s를 초과할 수 없습니다.".formatted(userMaxBalance));
            }
        });

        this.point.ifPresent(point -> {
            String message = "이미 존재하는 포인트입니다. id=%d, key=%s".formatted(point.id(), point.pointKey());
            throw new IllegalStateException(message);
        });
    }
}
