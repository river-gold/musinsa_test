package musinsa.test.api.point.earn;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Schema(description = "포인트 충전 응답")
public record PointEarnResponse(
        @Schema(description = "유저 식별키", requiredMode = Schema.RequiredMode.REQUIRED) Long userId,
        @Schema(description = "포인트 식별키", requiredMode = Schema.RequiredMode.REQUIRED) String pointKey,
        @Schema(description = "포인트 충전금액", requiredMode = Schema.RequiredMode.REQUIRED) BigDecimal amount,
        @Schema(description = "포인트 만료일", requiredMode = Schema.RequiredMode.REQUIRED) LocalDate expireDate,
        @Schema(description = "포인트 상태", requiredMode = Schema.RequiredMode.REQUIRED) PointStatus status) {
    static public PointEarnResponse of(Point point) {
        return PointEarnResponse.builder()
                .userId(point.userId())
                .pointKey(point.pointKey())
                .amount(point.earnedAmount())
                .expireDate(point.expireDate())
                .status(point.status())
                .build();
    }
}
