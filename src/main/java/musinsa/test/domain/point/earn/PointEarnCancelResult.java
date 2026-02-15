package musinsa.test.domain.point.earn;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import musinsa.test.domain.point.Point;

import java.math.BigDecimal;

@Getter
@Builder
@EqualsAndHashCode
public class PointEarnCancelResult {
    private final BigDecimal beforeBalance;
    private final BigDecimal canceledAmount;
    private final Point point;
}
