package musinsa.test.domain.point.use;

import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointEarnType;
import musinsa.test.domain.point.code.PointStatus;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointUseExecutorTest {
    @Test
    void 사용_에러_테스트() {
        // 잔액이 부족할때 에러
        {
            List<Point> points = List.of(
                    Instancio.of(Point.class)
                            .set(field(Point::balance), BigDecimal.ONE)
                            .set(field(Point::expireDate), LocalDate.MAX)
                            .create()
            );
            PointUseCommand command = Instancio.of(PointUseCommand.class)
                    .set(field(PointUseCommand::getAmount), BigDecimal.TEN)
                    .create();

            PointUseExecutor executor = new PointUseExecutor(points);

            Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> executor.execute(command));
        }
        // 상태가 적립이 아니면 에러(취소)
        {
            List<Point> points = List.of(
                    Instancio.of(Point.class)
                            .set(field(Point::balance), BigDecimal.TEN)
                            .set(field(Point::status), PointStatus.CANCEL)
                            .set(field(Point::expireDate), LocalDate.MAX)
                            .create()
            );
            PointUseCommand command = Instancio.of(PointUseCommand.class)
                    .set(field(PointUseCommand::getAmount), BigDecimal.TEN)
                    .create();

            PointUseExecutor executor = new PointUseExecutor(points);

            Assertions.assertThrowsExactly(IllegalStateException.class, () -> executor.execute(command));
        }
        // 상태가 적립이 아니면 에러(만료)
        {
            List<Point> points = List.of(
                    Instancio.of(Point.class)
                            .set(field(Point::balance), BigDecimal.TEN)
                            .set(field(Point::status), PointStatus.EXPIRED)
                            .set(field(Point::expireDate), LocalDate.MIN)
                            .create()
            );
            PointUseCommand command = Instancio.of(PointUseCommand.class)
                    .set(field(PointUseCommand::getAmount), BigDecimal.TEN)
                    .create();

            PointUseExecutor executor = new PointUseExecutor(points);

            Assertions.assertThrowsExactly(IllegalStateException.class, () -> executor.execute(command));
        }
    }

    @Test
    void 포인트1건_잔액10_사용1() {
        Point point = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.TEN)
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .create();
        PointUseCommand command = Instancio.of(PointUseCommand.class)
                .set(field(PointUseCommand::getAmount), BigDecimal.ONE)
                .create();

        PointUseExecutor executor = new PointUseExecutor(List.of(point));

        List<PointUseResults.Result> expected = List.of(
                PointUseResults.Result.builder()
                        .beforeBalance(BigDecimal.TEN)
                        .usedAmount(BigDecimal.ONE)
                        .point(point.withBalance(BigDecimal.valueOf(9)))
                        .build()
        );
        List<PointUseResults.Result> actual = executor.execute(command);

        assertEquals(expected, actual);
    }

    @Test
    void 포인트2건_잔액10_사용10() {
        Point point1 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(3))
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .set(field(Point::earnType), PointEarnType.SYSTEM)
                .set(field(Point::id), 1L)
                .create();
        Point point2 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(9))
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .set(field(Point::earnType), PointEarnType.SYSTEM)
                .set(field(Point::id), 2L)
                .create();
        PointUseCommand command = Instancio.of(PointUseCommand.class)
                .set(field(PointUseCommand::getAmount), BigDecimal.TEN)
                .create();

        PointUseExecutor executor = new PointUseExecutor(List.of(point1, point2));

        List<PointUseResults.Result> expected = List.of(
                PointUseResults.Result.builder()
                        .beforeBalance(BigDecimal.valueOf(3))
                        .usedAmount(BigDecimal.valueOf(3))
                        .point(point1.withBalance(BigDecimal.ZERO))
                        .build(),
                PointUseResults.Result.builder()
                        .beforeBalance(BigDecimal.valueOf(9))
                        .usedAmount(BigDecimal.valueOf(7))
                        .point(point2.withBalance(BigDecimal.valueOf(2)))
                        .build()
        );
        List<PointUseResults.Result> actual = executor.execute(command);

        assertEquals(expected, actual);
    }

    @Test
    void 관리자포인트1건_일반포인트2건_만료일다름_잔액20_사용20() {
        // 어드민 포인트 먼저 사용, 만료일 짧은것 부터 사용
        Point point1 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(9))
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.now())
                .set(field(Point::earnType), PointEarnType.SYSTEM)
                .set(field(Point::id), 1L)
                .create();
        Point point2 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(20))
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .set(field(Point::earnType), PointEarnType.SYSTEM)
                .set(field(Point::id), 2L)
                .create();
        Point point3 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(3))
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .set(field(Point::earnType), PointEarnType.ADMIN)
                .set(field(Point::id), 3L)
                .create();
        PointUseCommand command = Instancio.of(PointUseCommand.class)
                .set(field(PointUseCommand::getAmount), BigDecimal.valueOf(20))
                .create();

        PointUseExecutor executor = new PointUseExecutor(List.of(point1, point2, point3));

        List<PointUseResults.Result> expected = List.of(
                PointUseResults.Result.builder()
                        .beforeBalance(BigDecimal.valueOf(3))
                        .usedAmount(BigDecimal.valueOf(3))
                        .point(point3.withBalance(BigDecimal.ZERO))
                        .build(),
                PointUseResults.Result.builder()
                        .beforeBalance(BigDecimal.valueOf(9))
                        .usedAmount(BigDecimal.valueOf(9))
                        .point(point1.withBalance(BigDecimal.ZERO))
                        .build(),
                PointUseResults.Result.builder()
                        .beforeBalance(BigDecimal.valueOf(20))
                        .usedAmount(BigDecimal.valueOf(8))
                        .point(point2.withBalance(BigDecimal.valueOf(12)))
                        .build()
        );
        List<PointUseResults.Result> actual = executor.execute(command);

        assertEquals(expected, actual);
    }
}
