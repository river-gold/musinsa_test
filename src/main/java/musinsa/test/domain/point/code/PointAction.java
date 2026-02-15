package musinsa.test.domain.point.code;

import lombok.Getter;

import java.util.stream.Stream;

public enum PointAction {
    EARN(0, "적립"),
    EARN_CANCEL(1, "적립취소"),
    USE(2, "사용"),
    USE_CANCEL(3, "사용취소"),
    ;

    @Getter
    private final Integer code;
    private final String description;

    PointAction(int code, String description) {
        this.code = code;
        this.description = description;
    }

    static public PointAction of(Integer code) {
        return Stream.of(PointAction.values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow();
    }
}
