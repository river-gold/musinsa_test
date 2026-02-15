package musinsa.test.api.point.use;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import musinsa.test.domain.point.transaction.PointTransactionSummary;

import java.math.BigDecimal;

@Builder
@Schema(description = "포인트 취소 응답")
public record PointUseCancelResponse(
        @Schema(description = "유저 식별키", requiredMode = Schema.RequiredMode.REQUIRED) Long userId,
        @Schema(description = "취소 전 포인트 잔액", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal beforeBalance,
        @Schema(description = "사용 금액", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal amount,
        @Schema(description = "포인트 잔액", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal balance) {
    public static PointUseCancelResponse of(PointTransactionSummary transactionSummary) {
        return PointUseCancelResponse.builder()
                .userId(transactionSummary.getUserId())
                .beforeBalance(transactionSummary.getBeforeSumBalance())
                .amount(transactionSummary.getAmount())
                .balance(transactionSummary.getSumBalance())
                .build();
    }
}
