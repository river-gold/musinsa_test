package musinsa.test.utils;


import musinsa.test.domain.point.Point;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BigDecimalUtilsTest {

    @Test
    @DisplayName("합계 테스트")
    public void sumTest() {
        List<Point> list = List.of(
                Instancio.of(Point.class).set(field(Point::balance), BigDecimal.TEN).create(),
                Instancio.of(Point.class).set(field(Point::balance), BigDecimal.TEN).create(),
                Instancio.of(Point.class).set(field(Point::balance), BigDecimal.TEN).create()
        );

        BigDecimal expected = BigDecimal.valueOf(30L);
        BigDecimal actual = BigDecimalUtils.sum(list, Point::balance);

        assertEquals(expected, actual);
    }

}
