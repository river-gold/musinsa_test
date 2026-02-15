package musinsa.test.domain.point.use;

import lombok.Builder;
import lombok.Getter;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointAction;
import musinsa.test.domain.point.code.PointReference;
import musinsa.test.domain.point.transaction.PointTransactionSummary;
import musinsa.test.domain.point.transaction.PointTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class PointUseCancelCommand {
    private final Long userId;
    private final BigDecimal amount;
    private final PointReference reference;
    private final String referenceKey;

    public PointTransactionSummary createTransaction(PointUseCancelResults results) {
        List<PointTransaction> transactions = results.getResults().stream()
                .map(result -> {
                    Point point = result.getPoint();
                    return PointTransaction.builder()
                            .pointId(point.id())
                            .status(point.status())
                            .beforeBalance(result.getBeforeBalance())
                            .amount(result.getCanceledAmount().abs())
                            .balance(point.balance())
                            .build();
                })
                .toList();

        return PointTransactionSummary.builder()
                .action(PointAction.USE_CANCEL)
                .userId(userId)
                .beforeSumBalance(results.getBeforeSumBalance())
                .amount(results.getCanceledAmount().abs())
                .sumBalance(results.getBeforeSumBalance().add(results.getCanceledAmount().abs()))
                .reference(Optional.of(reference))
                .referenceKey(Optional.of(referenceKey))
                .transactions(transactions)
                .build();
    }
}
