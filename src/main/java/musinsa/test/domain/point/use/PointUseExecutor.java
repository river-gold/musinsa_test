package musinsa.test.domain.point.use;

import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record PointUseExecutor(List<Point> points) {
    public List<PointUseResults.Result> execute(PointUseCommand command) {
        BigDecimal sumBalance = Point.sumBalance(points);

        if (sumBalance.compareTo(command.getAmount()) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        List<Point> sortedPoints = this.points.stream()
                // 관리자 지급 우선, 만료일 빠른 순서
                .sorted(
                        Comparator.comparing(Point::earnType, Comparator.reverseOrder())
                                .thenComparing(Point::expireDate)
                                .thenComparing(Point::id)
                )
                .toList();

        List<PointUseResults.Result> results = new ArrayList<>();
        BigDecimal remainingAmount = command.getAmount();

        // 포인트 별로 사용금액 차감
        for (Point point : sortedPoints) {
            if (point.status() != PointStatus.EARNED) {
                throw new IllegalStateException("포인트가 사용 가능한 포인트가 아닙니다.");
            }

            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal beforeBalance = point.balance();

            // 현재 포인트에서 차감할 금액 계산
            BigDecimal deduction = beforeBalance.compareTo(remainingAmount) < 0
                    ? beforeBalance : remainingAmount;
            BigDecimal useAfterBalance = beforeBalance.subtract(deduction);

            // 사용한 포인트 리스트 업데이트
            results.add(
                    PointUseResults.Result.builder()
                            .beforeBalance(beforeBalance)
                            .usedAmount(deduction)
                            .point(point.withBalance(useAfterBalance))
                            .build()
            );

            remainingAmount = remainingAmount.subtract(deduction);
        }

        return results;
    }

    @Component
    static class Factory {
        public PointUseExecutor create(List<Point> points) {
            return new PointUseExecutor(points);
        }
    }
}
