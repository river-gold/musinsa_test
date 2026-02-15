package musinsa.test.api.point.use;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import musinsa.test.api.common.ApiResponse;
import musinsa.test.domain.point.transaction.PointTransactionSummary;
import musinsa.test.domain.point.use.PointUseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointUseController {
    private final PointUseService pointUseService;

    @PostMapping(":use")
    @Operation(summary = "포인트 사용")
    public ApiResponse<PointUseResponse> use(@RequestBody PointUseRequest request) {
        PointTransactionSummary transactionSummary = pointUseService.use(request.toCommand());
        PointUseResponse response = PointUseResponse.of(transactionSummary);
        return ApiResponse.of(response);
    }

    @PostMapping(":useCancel")
    @Operation(summary = "포인트 취소")
    public ApiResponse<PointUseCancelResponse> cancel(@RequestBody PointUseCancelRequest request) {
        PointTransactionSummary transactionSummary = pointUseService.cancel(request.toCommand());
        return ApiResponse.of(PointUseCancelResponse.of(transactionSummary));
    }
}
