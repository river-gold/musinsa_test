package musinsa.test.api.point.earn;

import io.swagger.v3.oas.annotations.media.Schema;
import musinsa.test.domain.point.earn.PointEarnCommand;

import java.math.BigDecimal;
import java.util.Optional;

@Schema(description = "포인트 충전 요청")
public record PointEarnRequest(@Schema(description = "유저 식별키", requiredMode = Schema.RequiredMode.REQUIRED) Long userId,
                               @Schema(description = "충전 포인트", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal amount,
                               @Schema(description = "만료일", requiredMode = Schema.RequiredMode.NOT_REQUIRED) Optional<Integer> expireDays) {
    public PointEarnCommand toCommand() {
        return PointEarnCommand.builder()
                .userId(userId)
                .amount(amount)
                .expireDays(expireDays)
                .issuerId(Optional.empty())
                .build();
    }
}
