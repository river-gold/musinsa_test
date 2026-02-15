package musinsa.test.api.point.earn;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import musinsa.test.api.common.ApiResponse;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.earn.PointEarnService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointEarnController {

    private final PointEarnService pointEarnService;

    @PostMapping(":earn")
    @Operation(summary = "포인트 충전")
    public ApiResponse<PointEarnResponse> earn(@RequestBody PointEarnRequest request) {
        Point point = pointEarnService.earn(request.toCommand());
        return ApiResponse.of(PointEarnResponse.of(point));
    }

    @PostMapping(":manualEarn")
    @Operation(summary = "관리자 포인트 충전")
    public ApiResponse<PointEarnResponse> manualEarn(@RequestBody PointManualEarnRequest request) {
        Point result = pointEarnService.earn(request.toCommand());
        return ApiResponse.of(PointEarnResponse.of(result));
    }

    @PatchMapping("/{pointKey}/:cancel")
    @Operation(summary = "포인트 충전 취소")
    public ApiResponse<PointEarnCancelResponse> cancel(
            @PathVariable("pointKey") String pointKey,
            @RequestBody PointEarnCancelRequest request
    ) {
        Point result = pointEarnService.cancel(request.toCommand(pointKey));
        return ApiResponse.of(PointEarnCancelResponse.of(result));
    }

}
