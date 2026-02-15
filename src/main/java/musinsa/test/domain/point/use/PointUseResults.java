package musinsa.test.domain.point.use;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import musinsa.test.domain.point.Point;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class PointUseResults {
    private final List<Result> results;
    private final BigDecimal beforeSumBalance;
    private final BigDecimal usedAmount;

    @Getter
    @Builder
    @EqualsAndHashCode
    @ToString
    public static class Result {
        private final BigDecimal beforeBalance;
        private final BigDecimal usedAmount;
        private final Point point;
    }
}
