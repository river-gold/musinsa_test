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
public class PointUseCommand {
    private final Long userId;
    private final BigDecimal amount;
    private final PointReference reference;
    private final String referenceKey;

    public PointTransactionSummary createTransaction(PointUseResults results) {
        List<PointTransaction> transactions = results.getResults().stream()
                .map(result -> {
                    Point point = result.getPoint();
                    return PointTransaction.builder()
                            .pointId(point.id())
                            .status(point.status())
                            .beforeBalance(result.getBeforeBalance())
                            .amount(result.getUsedAmount().abs().negate())
                            .balance(point.balance())
                            .build();
                })
                .toList();

        return PointTransactionSummary.builder()
                .action(PointAction.USE)
                .userId(userId)
                .beforeSumBalance(results.getBeforeSumBalance())
                .amount(results.getUsedAmount().abs().negate())
                .sumBalance(results.getBeforeSumBalance().subtract(results.getUsedAmount().abs()))
                .reference(Optional.of(reference))
                .referenceKey(Optional.of(referenceKey))
                .transactions(transactions)
                .build();
    }
}
