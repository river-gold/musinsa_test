package musinsa.test.domain.point.earn;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointAction;
import musinsa.test.domain.point.code.PointEarnType;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.domain.point.transaction.PointTransactionSummary;
import musinsa.test.domain.point.transaction.PointTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Builder
public class PointEarnCommand {
    @Builder.Default
    private final String pointKey = UUID.randomUUID().toString();
    private final Long userId;
    private final BigDecimal amount;
    @Builder.Default
    private final Optional<Integer> expireDays = Optional.empty();
    @Builder.Default
    private final Optional<PointEarnType> earnType = Optional.empty();
    @Builder.Default
    private final Optional<String> issuerId = Optional.empty();

    public Point createPoint() {
        return Point.builder()
//                .pointKey(pointKey)
                .userId(userId)
                .earnedAmount(amount)
                .balance(amount)
                .expireDate(LocalDate.now().plusDays(expireDays.orElse(365)))
                .status(PointStatus.EARNED)
                .earnType(earnType.orElse(PointEarnType.SYSTEM))
                .issuerId(issuerId)
                .build();
    }

    public PointTransactionSummary createTransaction(PointEarnResult earnResult) {
        Point point = earnResult.getPoint();
        List<PointTransaction> transactions = List.of(
                PointTransaction.builder()
                        .pointId(point.id())
                        .status(point.status())
                        .beforeBalance(BigDecimal.ZERO)
                        .amount(point.earnedAmount())
                        .balance(point.balance())
                        .build()
        );
        return PointTransactionSummary.builder()
                .action(PointAction.EARN)
                .userId(userId)
                .beforeSumBalance(earnResult.getBeforeBalance())
                .amount(earnResult.getEarnedAmount().abs())
                .sumBalance(earnResult.getBeforeBalance().add(earnResult.getEarnedAmount().abs()))
                .reference(Optional.empty())
                .referenceKey(Optional.empty())
                .transactions(transactions)
                .build();
    }
}
