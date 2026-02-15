package musinsa.test.api.point.earn;

import io.swagger.v3.oas.annotations.media.Schema;
import musinsa.test.domain.point.earn.PointEarnCancelCommand;

@Schema(description = "포인트 충전 취소 요청")
public record PointEarnCancelRequest(
        @Schema(description = "유저 식별키", requiredMode = Schema.RequiredMode.REQUIRED) Long userId) {
    public PointEarnCancelCommand toCommand(String pointKey) {
        return PointEarnCancelCommand.builder()
                .pointKey(pointKey)
                .userId(userId)
                .build();
    }
}
