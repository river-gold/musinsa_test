package musinsa.test.domain.point;

import musinsa.test.domain.point.code.PointStatus;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointTest {
    @Test
    public void getStatusTest() {

        assertEquals(
                PointStatus.EARNED,
                Instancio.of(Point.class)
                        .set(field(Point::expireDate), LocalDate.now())
                        .create()
                        .withStatus(PointStatus.EARNED)
                        .status()
        );
        assertEquals(
                PointStatus.EARNED,
                Instancio.of(Point.class)
                        .set(field(Point::expireDate), LocalDate.now().plusDays(1))
                        .create()
                        .withStatus(PointStatus.EARNED)
                        .status()
        );
        assertEquals(
                PointStatus.EXPIRED,
                Instancio.of(Point.class)
                        .set(field(Point::expireDate), LocalDate.now().minusDays(1))
                        .create()
                        .withStatus(PointStatus.EARNED)
                        .status()
        );
        assertEquals(
                PointStatus.CANCEL,
                Instancio.of(Point.class)
                        .set(field(Point::expireDate), LocalDate.now().minusDays(1))
                        .create()
                        .withStatus(PointStatus.CANCEL)
                        .status()
        );
        assertEquals(
                PointStatus.EXHAUSTED,
                Instancio.of(Point.class)
                        .set(field(Point::balance), BigDecimal.ZERO)
                        .set(field(Point::expireDate), LocalDate.now().minusDays(1))
                        .create()
                        .withStatus(PointStatus.EARNED)
                        .status()
        );
    }

    @Test
    @DisplayName("cancel하면 잔액이 0원, 상태가 CANCEL이 된다.")
    public void cancel() {
        Point point = Instancio.create(Point.class)
                .withBalance(BigDecimal.TEN)
                .withStatus(PointStatus.EARNED)
                .cancel();

        assertEquals(BigDecimal.ZERO, point.balance());
        assertEquals(PointStatus.CANCEL, point.status());
    }
}
