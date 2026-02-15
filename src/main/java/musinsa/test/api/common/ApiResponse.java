package musinsa.test.api.common;

import lombok.Builder;

import java.util.Optional;

@Builder
public record ApiResponse<T>(ApiStatus status, Optional<String> message, Optional<T> data) {
    static public <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(ApiStatus.SUCCESS, Optional.empty(), Optional.of(data));
    }

    static public <T> ApiResponse<T> of(ApiStatus status, Exception e) {
        return new ApiResponse<>(status, Optional.of(e.getMessage()), Optional.empty());
    }
}
