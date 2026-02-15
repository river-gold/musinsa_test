package musinsa.test.domain.point.code;

import lombok.Getter;

import java.util.stream.Stream;

public enum PointStatus {
    EARNED(0, "적립"),
    CANCEL(1, "취소"),
    EXPIRED(2, "만료"),
    EXHAUSTED(3, "사용완료");

    @Getter
    private final Integer code;
    @Getter
    private final String description;

    PointStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    static public PointStatus of(Integer code) {
        return Stream.of(PointStatus.values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow();
    }
}
