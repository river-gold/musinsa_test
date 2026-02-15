package musinsa.test.domain.point.earn;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import musinsa.test.domain.point.Point;

import java.math.BigDecimal;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class PointEarnResult {
    private final BigDecimal beforeBalance;
    private final BigDecimal earnedAmount;
    private final Point point;
}
