package musinsa.test.api.point.use;

import io.swagger.v3.oas.annotations.media.Schema;
import musinsa.test.domain.point.code.PointReference;
import musinsa.test.domain.point.use.PointUseCommand;

import java.math.BigDecimal;

@Schema(description = "포인트 사용 요청")
public record PointUseRequest(@Schema(description = "유저 식별키", requiredMode = Schema.RequiredMode.REQUIRED) Long userId,
                              @Schema(description = "사용 포인트", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal amount,
                              @Schema(description = "요청자 유형", requiredMode = Schema.RequiredMode.REQUIRED) PointReference reference,
                              @Schema(description = "요청자 식별키", requiredMode = Schema.RequiredMode.REQUIRED) String referenceKey) {
    public PointUseCommand toCommand() {
        return PointUseCommand.builder()
                .userId(userId)
                .amount(amount)
                .reference(reference)
                .referenceKey(referenceKey)
                .build();
    }
}
