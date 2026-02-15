package musinsa.test.domain.point.transaction;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import musinsa.test.domain.point.code.PointAction;
import musinsa.test.domain.point.code.PointReference;
import musinsa.test.entity.point.PointTransactionSummaryEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class PointTransactionSummary {
    private final Long id;
    private final PointAction action;
    private final Long userId;
    private final BigDecimal beforeSumBalance;
    private final BigDecimal amount;
    private final BigDecimal sumBalance;
    private final Optional<PointReference> reference;
    private final Optional<String> referenceKey;
    private final List<PointTransaction> transactions;

    public static PointTransactionSummary of(PointTransactionSummaryEntity entity, List<PointTransaction> transactions) {
        return PointTransactionSummary.builder()
                .id(entity.getId())
                .action(entity.getAction())
                .userId(entity.getUserId())
                .beforeSumBalance(entity.getBeforeSumBalance())
                .amount(entity.getAmount())
                .sumBalance(entity.getSumBalance())
                .reference(Optional.ofNullable(entity.getReference()))
                .referenceKey(Optional.ofNullable(entity.getReferenceKey()))
                .transactions(transactions)
                .build();
    }

    public static List<PointTransactionSummary> list(List<PointTransactionSummaryEntity> entities, List<PointTransaction> transactions) {
        Map<Long, List<PointTransaction>> transactionMap = transactions.stream().collect(Collectors.groupingBy(PointTransaction::getSummaryId));
        return entities.stream()
                .map(t -> of(t, transactionMap.get(t.getId())))
                .toList();
    }

    public PointTransactionSummaryEntity createEntity() {
        return PointTransactionSummaryEntity.builder()
                .action(getAction())
                .userId(getUserId())
                .beforeSumBalance(getBeforeSumBalance())
                .amount(getAmount())
                .sumBalance(getSumBalance())
                .reference(getReference().orElse(null))
                .referenceKey(getReferenceKey().orElse(null))
                .build();
    }
}
