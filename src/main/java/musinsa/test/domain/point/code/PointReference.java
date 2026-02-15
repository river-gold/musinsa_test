package musinsa.test.domain.point.code;

import lombok.Getter;

import java.util.stream.Stream;

public enum PointReference {
    ORDER(0, "주문"),
    ;

    @Getter
    private final Integer code;
    private final String description;

    PointReference(int code, String description) {
        this.code = code;
        this.description = description;
    }

    static public PointReference of(Integer code) {
        return Stream.of(PointReference.values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow();
    }
}
