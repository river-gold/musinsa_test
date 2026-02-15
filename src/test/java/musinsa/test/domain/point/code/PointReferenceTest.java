package musinsa.test.domain.point.code;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointReferenceTest {
    @ParameterizedTest
    @EnumSource
    void ofTest(PointReference code) {
        assertEquals(code, PointReference.of(code.getCode()));
    }
}
