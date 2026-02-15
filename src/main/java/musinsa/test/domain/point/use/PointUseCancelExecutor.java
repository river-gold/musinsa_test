package musinsa.test.domain.point.use;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointAction;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.domain.point.earn.PointEarnCommand;
import musinsa.test.domain.point.transaction.PointTransactionSummary;
import musinsa.test.domain.point.transaction.PointTransaction;
import musinsa.test.utils.BigDecimalUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class PointUseCancelExecutor {
    private final List<PointTransactionSummary> transactionSummaries;

    public List<PointUseCancelResults.Result> execute(PointUseCancelCommand command) {
        BigDecimal sumUsedAmount = BigDecimalUtils.sum(transactionSummaries, PointTransactionSummary::getAmount).abs();

        if (sumUsedAmount.compareTo(command.getAmount()) < 0) {
            throw new IllegalArgumentException("취소할 금액이 사용된 금액을 초과합니다.");
        }

        List<PointTransactionSummary> usedTransactionSummaries = transactionSummaries.stream().filter(t -> PointAction.USE == t.getAction()).toList();

        if (usedTransactionSummaries.size() != 1) {
            throw new IllegalStateException("사용 내역이 1건이 아닙니다.");
        }

        PointTransactionSummary usedTransactionSummary = usedTransactionSummaries.getFirst();
        // 포인트 사용순으로 취소하기 위해 정렬
        List<PointTransaction> usedTransactions = usedTransactionSummary.getTransactions().stream().sorted(Comparator.comparing(PointTransaction::getId)).toList();
        List<PointUseCancelResults.Result> results = new ArrayList<>();
        BigDecimal remainingAmount = command.getAmount();

        for (PointTransaction transaction : usedTransactions) {
            Point point = transaction.getPoint();

            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal cancelBeforeBalance = point.balance();

            // 현재 포인트에서 합산할 금액 계산
            BigDecimal addition = cancelBeforeBalance.add(remainingAmount).compareTo(point.earnedAmount()) > 0
                    ? point.earnedAmount().subtract(cancelBeforeBalance) : remainingAmount;

            if (transaction.getAmount().abs().compareTo(addition) < 0) {
                throw new IllegalStateException("사용 금액보다 큰 금액은 취소 할수 없습니다.");
            }

            BigDecimal cancelAfterBalance = cancelBeforeBalance.add(addition);

            PointUseCancelResults.Result result = switch (point.status()) {
                case PointStatus.EARNED, PointStatus.EXHAUSTED -> PointUseCancelResults.Result.builder()
                        .beforeBalance(cancelBeforeBalance)
                        .canceledAmount(addition)
                        .point(point.withBalance(cancelAfterBalance))
                        .build();
                // 포인트 상태가 만료면 신규 포인트 생성
                case PointStatus.EXPIRED -> PointUseCancelResults.Result.builder()
                        .beforeBalance(BigDecimal.ZERO)
                        .canceledAmount(addition)
                        .point(
                                PointEarnCommand
                                        .builder()
                                        .userId(command.getUserId())
                                        .amount(addition)
                                        .build()
                                        .createPoint()
                        )
                        .build();
                default -> throw new IllegalStateException("포인트 상태를 확인해주세요.");
            };

            results.add(result);

            remainingAmount = remainingAmount.subtract(addition);
        }

        return results;
    }

    @Component
    static class Factory {
        public PointUseCancelExecutor create(List<PointTransactionSummary> transactionSummaries) {
            return new PointUseCancelExecutor(transactionSummaries);
        }
    }
}
