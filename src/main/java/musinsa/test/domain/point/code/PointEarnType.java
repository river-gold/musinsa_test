package musinsa.test.domain.point.code;

import lombok.Getter;

import java.util.stream.Stream;

public enum PointEarnType {
    SYSTEM(0, "시스템"),
    ADMIN(1, "관리자");

    @Getter
    private final Integer code;
    @Getter
    private final String description;

    PointEarnType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    static public PointEarnType of(Integer code) {
        return Stream.of(PointEarnType.values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow();
    }
}
