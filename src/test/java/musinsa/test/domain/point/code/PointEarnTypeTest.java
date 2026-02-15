package musinsa.test.domain.point.code;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointEarnTypeTest {
    @ParameterizedTest
    @EnumSource
    void ofTest(PointEarnType code) {
        assertEquals(code, PointEarnType.of(code.getCode()));
    }
}
