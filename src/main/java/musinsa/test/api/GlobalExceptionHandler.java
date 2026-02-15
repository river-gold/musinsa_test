package musinsa.test.api;

import lombok.extern.slf4j.Slf4j;
import musinsa.test.api.common.ApiResponse;
import musinsa.test.api.common.ApiStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> exceptionHandler(Exception e) {
        log.error("에러 발생", e);
        return ResponseEntity.internalServerError().body(ApiResponse.of(ApiStatus.ERROR, e));
    }

}
