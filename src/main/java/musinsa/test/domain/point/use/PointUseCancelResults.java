package musinsa.test.domain.point.use;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import musinsa.test.domain.point.Point;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class PointUseCancelResults {
    private final List<Result> results;
    private final BigDecimal beforeSumBalance;
    private final BigDecimal canceledAmount;

    @Getter
    @Builder
    @ToString
    @EqualsAndHashCode
    public static class Result {
        private final BigDecimal beforeBalance;
        private final BigDecimal canceledAmount;
        @With
        private final Point point;
    }
}
