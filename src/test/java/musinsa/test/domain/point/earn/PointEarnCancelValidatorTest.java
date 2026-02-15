package musinsa.test.domain.point.earn;

import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointStatus;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class PointEarnCancelValidatorTest {
    @Test
    void validateTest() {
        PointEarnCancelCommand command = Instancio.create(PointEarnCancelCommand.class);

        // 포인트 없으면 에러 발생
        {
            PointEarnCancelValidator validator = new PointEarnCancelValidator(Optional.empty());
            assertThrowsExactly(IllegalArgumentException.class, () -> validator.validate(command));
        }

        // 포인트 상태가 지급이 아니면 에러
        {
            List.of(PointStatus.CANCEL, PointStatus.EXPIRED).forEach(pointStatus -> {
                Point point = Instancio.create(Point.class).withStatus(pointStatus);
                PointEarnCancelValidator validator = new PointEarnCancelValidator(Optional.of(point));
                assertThrowsExactly(IllegalArgumentException.class, () -> validator.validate(command));
            });
        }

        // 포인트 잔액이 최초 충전금액과 다르면 에러
        {
            Point point = Instancio.of(Point.class)
                    .set(field(Point::expireDate), LocalDate.MAX)
                    .set(field(Point::balance), BigDecimal.ZERO)
                    .set(field(Point::earnedAmount), BigDecimal.TEN)
                    .create()
                    .withStatus(PointStatus.EARNED);
            PointEarnCancelValidator validator = new PointEarnCancelValidator(Optional.of(point));
            assertThrowsExactly(IllegalArgumentException.class, () -> validator.validate(command));
        }

        // 에러 없이 정상 실행
        {
            Point point = Instancio.of(Point.class)
                    .set(field(Point::expireDate), LocalDate.MAX)
                    .set(field(Point::balance), BigDecimal.TEN)
                    .set(field(Point::earnedAmount), BigDecimal.TEN)
                    .create()
                    .withStatus(PointStatus.EARNED);
            PointEarnCancelValidator validator = new PointEarnCancelValidator(Optional.of(point));
            Assertions.assertDoesNotThrow(() -> validator.validate(command));
        }
    }
}
