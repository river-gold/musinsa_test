package musinsa.test.domain.point.earn;

import lombok.Builder;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointAction;
import musinsa.test.domain.point.transaction.PointTransactionSummary;
import musinsa.test.domain.point.transaction.PointTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Builder
public record PointEarnCancelCommand(String pointKey, Long userId) {
    public PointTransactionSummary createTransaction(PointEarnCancelResult result) {
        Point point = result.getPoint();
        List<PointTransaction> transactions = List.of(
                PointTransaction.builder()
                        .pointId(point.id())
                        .status(point.status())
                        .beforeBalance(result.getBeforeBalance())
                        .amount(result.getCanceledAmount().abs().negate())
                        .balance(BigDecimal.ZERO)
                        .build()
        );
        return PointTransactionSummary.builder()
                .action(PointAction.EARN_CANCEL)
                .userId(userId)
                .beforeSumBalance(result.getBeforeBalance())
                .amount(result.getCanceledAmount().abs().negate())
                .sumBalance(result.getBeforeBalance().subtract(result.getCanceledAmount().abs()))
                .reference(Optional.empty())
                .referenceKey(Optional.empty())
                .transactions(transactions)
                .build();
    }
}
