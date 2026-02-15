package musinsa.test.domain.point.use;

import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.code.PointAction;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.domain.point.earn.PointEarnCommand;
import musinsa.test.domain.point.transaction.PointTransactionSummary;
import musinsa.test.domain.point.transaction.PointTransaction;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointUseCancelExecutorTest {
    private MockedStatic<UUID> mockedUUID;

    @Test
    void 사용취소_에러_테스트() {
        // 취소 금액이 사용된 금액 초과하면 에러
        {
            PointUseCancelCommand command = Instancio.of(PointUseCancelCommand.class)
                    .set(field(PointUseCancelCommand::getAmount), BigDecimal.valueOf(11))
                    .create();
            List<PointTransactionSummary> transactionSummaries = List.of(
                    Instancio.of(PointTransactionSummary.class)
                            .set(field(PointTransactionSummary::getAmount), BigDecimal.TEN)
                            .create()
            );

            PointUseCancelExecutor executor = new PointUseCancelExecutor(transactionSummaries);
            Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> executor.execute(command));
        }
        // 사용 내역이 1건이 아니면 에러
        {
            PointUseCancelCommand command = Instancio.of(PointUseCancelCommand.class)
                    .set(field(PointUseCancelCommand::getAmount), BigDecimal.TEN)
                    .create();
            List<PointTransactionSummary> transactionSummaries = List.of(
                    Instancio.of(PointTransactionSummary.class)
                            .set(field(PointTransactionSummary::getAmount), BigDecimal.valueOf(5))
                            .set(field(PointTransactionSummary::getAction), PointAction.USE_CANCEL)
                            .create(),
                    Instancio.of(PointTransactionSummary.class)
                            .set(field(PointTransactionSummary::getAmount), BigDecimal.valueOf(5))
                            .set(field(PointTransactionSummary::getAction), PointAction.USE_CANCEL)
                            .create()
            );

            PointUseCancelExecutor executor = new PointUseCancelExecutor(transactionSummaries);
            Assertions.assertThrowsExactly(IllegalStateException.class, () -> executor.execute(command));
        }
        // 포인트 상태가 적립, 만료가 아니면 에러 발생
        {
            PointUseCancelCommand command = Instancio.of(PointUseCancelCommand.class)
                    .set(field(PointUseCancelCommand::getAmount), BigDecimal.TEN)
                    .create();
            Point point = Instancio.of(Point.class)
                    .set(field(Point::status), PointStatus.CANCEL)
                    .create();
            List<PointTransaction> transaction = List.of(
                    Instancio.of(PointTransaction.class)
                            .set(field(PointTransaction::getPoint), point)
                            .create()
            );
            List<PointTransactionSummary> transactionSummaries = List.of(
                    Instancio.of(PointTransactionSummary.class)
                            .set(field(PointTransactionSummary::getAmount), BigDecimal.TEN)
                            .set(field(PointTransactionSummary::getTransactions), transaction)
                            .create()
            );

            PointUseCancelExecutor executor = new PointUseCancelExecutor(transactionSummaries);
            Assertions.assertThrowsExactly(IllegalStateException.class, () -> executor.execute(command));
        }
        // PointTransaction.amount보다 큰 금액을 취소 할때 에러
        {
            PointUseCancelCommand command = Instancio.of(PointUseCancelCommand.class)
                    .set(field(PointUseCancelCommand::getAmount), BigDecimal.TWO)
                    .create();
            Point point = Instancio.of(Point.class)
                    .set(field(Point::balance), BigDecimal.TEN)
                    .set(field(Point::earnedAmount), BigDecimal.valueOf(20))
                    .set(field(Point::status), PointStatus.EARNED)
                    .set(field(Point::expireDate), LocalDate.MAX)
                    .create();
            List<PointTransaction> transactions = List.of(
                    Instancio.of(PointTransaction.class)
                            .set(field(PointTransaction::getPoint), point)
                            .set(field(PointTransaction::getAmount), BigDecimal.ONE)
                            .create()
            );
            List<PointTransactionSummary> transactionSummaries = List.of(
                    Instancio.of(PointTransactionSummary.class)
                            .set(field(PointTransactionSummary::getAction), PointAction.USE)
                            .set(field(PointTransactionSummary::getAmount), BigDecimal.TEN)
                            .set(field(PointTransactionSummary::getTransactions), transactions)
                            .create()
            );

            PointUseCancelExecutor executor = new PointUseCancelExecutor(transactionSummaries);
            Assertions.assertThrowsExactly(IllegalStateException.class, () -> executor.execute(command));
        }
    }

    @Test
    void 사용취소_포인트1건_1포인트사용_1포인트취소() {
        PointUseCancelCommand command = Instancio.of(PointUseCancelCommand.class)
                .set(field(PointUseCancelCommand::getAmount), BigDecimal.ONE)
                .create();
        Point point = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.TEN)
                .set(field(Point::earnedAmount), BigDecimal.valueOf(20))
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .create();
        List<PointTransaction> transactions = List.of(
                Instancio.of(PointTransaction.class)
                        .set(field(PointTransaction::getPoint), point)
                        .set(field(PointTransaction::getAmount), BigDecimal.ONE)
                        .create()
        );
        List<PointTransactionSummary> transactionSummaries = List.of(
                Instancio.of(PointTransactionSummary.class)
                        .set(field(PointTransactionSummary::getAction), PointAction.USE)
                        .set(field(PointTransactionSummary::getAmount), BigDecimal.TEN)
                        .set(field(PointTransactionSummary::getTransactions), transactions)
                        .create()
        );

        PointUseCancelExecutor executor = new PointUseCancelExecutor(transactionSummaries);


        List<PointUseCancelResults.Result> expected = List.of(
                PointUseCancelResults.Result.builder()
                        .beforeBalance(BigDecimal.TEN)
                        .canceledAmount(BigDecimal.ONE)
                        .point(point.withBalance(BigDecimal.valueOf(11)))
                        .build()
        );
        List<PointUseCancelResults.Result> actual = executor.execute(command);

        assertEquals(expected, actual);
    }

    @Test
    void 사용취소_포인트2건_12포인트사용_10포인트취소() {
        PointUseCancelCommand command = Instancio.of(PointUseCancelCommand.class)
                .set(field(PointUseCancelCommand::getAmount), BigDecimal.TEN)
                .create();
        Point point1 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(8))
                .set(field(Point::earnedAmount), BigDecimal.TEN)
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .create();
        Point point2 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.TEN)
                .set(field(Point::earnedAmount), BigDecimal.valueOf(20))
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .create();
        List<PointTransaction> transactions = List.of(
                Instancio.of(PointTransaction.class)
                        .set(field(PointTransaction::getId), 1L)
                        .set(field(PointTransaction::getPoint), point1)
                        .set(field(PointTransaction::getAmount), BigDecimal.TWO)
                        .create(),
                Instancio.of(PointTransaction.class)
                        .set(field(PointTransaction::getId), 2L)
                        .set(field(PointTransaction::getPoint), point2)
                        .set(field(PointTransaction::getAmount), BigDecimal.TEN)
                        .create()
        );
        List<PointTransactionSummary> transactionSummaries = List.of(
                Instancio.of(PointTransactionSummary.class)
                        .set(field(PointTransactionSummary::getAction), PointAction.USE)
                        .set(field(PointTransactionSummary::getAmount), BigDecimal.valueOf(12))
                        .set(field(PointTransactionSummary::getTransactions), transactions)
                        .create()
        );

        PointUseCancelExecutor executor = new PointUseCancelExecutor(transactionSummaries);


        List<PointUseCancelResults.Result> expected = List.of(
                PointUseCancelResults.Result.builder()
                        .beforeBalance(BigDecimal.valueOf(8))
                        .canceledAmount(BigDecimal.valueOf(2))
                        .point(point1.withBalance(BigDecimal.TEN))
                        .build(),
                PointUseCancelResults.Result.builder()
                        .beforeBalance(BigDecimal.TEN)
                        .canceledAmount(BigDecimal.valueOf(8))
                        .point(point2.withBalance(BigDecimal.valueOf(18)))
                        .build()
        );
        List<PointUseCancelResults.Result> actual = executor.execute(command);

        assertEquals(expected, actual);
    }

    @Test
    void 사용취소_포인트2건_12포인트사용_12포인트취소() {
        PointUseCancelCommand command = Instancio.of(PointUseCancelCommand.class)
                .set(field(PointUseCancelCommand::getAmount), BigDecimal.valueOf(12))
                .create();
        Point point1 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(4))
                .set(field(Point::earnedAmount), BigDecimal.TEN)
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .create();
        Point point2 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(6))
                .set(field(Point::earnedAmount), BigDecimal.valueOf(20))
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .create();
        List<PointTransaction> transactions = List.of(
                Instancio.of(PointTransaction.class)
                        .set(field(PointTransaction::getId), 1L)
                        .set(field(PointTransaction::getPoint), point1)
                        .set(field(PointTransaction::getAmount), BigDecimal.valueOf(6))
                        .create(),
                Instancio.of(PointTransaction.class)
                        .set(field(PointTransaction::getId), 2L)
                        .set(field(PointTransaction::getPoint), point2)
                        .set(field(PointTransaction::getAmount), BigDecimal.valueOf(6))
                        .create()
        );
        List<PointTransactionSummary> transactionSummaries = List.of(
                Instancio.of(PointTransactionSummary.class)
                        .set(field(PointTransactionSummary::getAction), PointAction.USE)
                        .set(field(PointTransactionSummary::getAmount), BigDecimal.valueOf(12))
                        .set(field(PointTransactionSummary::getTransactions), transactions)
                        .create()
        );

        PointUseCancelExecutor executor = new PointUseCancelExecutor(transactionSummaries);


        List<PointUseCancelResults.Result> expected = List.of(
                PointUseCancelResults.Result.builder()
                        .beforeBalance(BigDecimal.valueOf(4))
                        .canceledAmount(BigDecimal.valueOf(6))
                        .point(point1.withBalance(BigDecimal.TEN))
                        .build(),
                PointUseCancelResults.Result.builder()
                        .beforeBalance(BigDecimal.valueOf(6))
                        .canceledAmount(BigDecimal.valueOf(6))
                        .point(point2.withBalance(BigDecimal.valueOf(12)))
                        .build()
        );
        List<PointUseCancelResults.Result> actual = executor.execute(command);

        assertEquals(expected, actual);
    }

    @Test
    void 사용취소_정상포인트2건_만료포인트1건_20포인트사용_10포인트취소() {
        PointUseCancelCommand command = Instancio.of(PointUseCancelCommand.class)
                .set(field(PointUseCancelCommand::getAmount), BigDecimal.TEN)
                .create();
        Point point1 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(8))
                .set(field(Point::earnedAmount), BigDecimal.TEN)
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .create();
        Point point2 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(7))
                .set(field(Point::earnedAmount), BigDecimal.TEN)
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MIN)
                .create();
        Point point3 = Instancio.of(Point.class)
                .set(field(Point::balance), BigDecimal.valueOf(5))
                .set(field(Point::earnedAmount), BigDecimal.valueOf(30))
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .create();
        List<PointTransaction> transactions = List.of(
                Instancio.of(PointTransaction.class)
                        .set(field(PointTransaction::getId), 1L)
                        .set(field(PointTransaction::getPoint), point1)
                        .set(field(PointTransaction::getAmount), BigDecimal.TWO)
                        .create(),
                Instancio.of(PointTransaction.class)
                        .set(field(PointTransaction::getId), 2L)
                        .set(field(PointTransaction::getPoint), point2)
                        .set(field(PointTransaction::getAmount), BigDecimal.valueOf(3))
                        .create(),
                Instancio.of(PointTransaction.class)
                        .set(field(PointTransaction::getId), 3L)
                        .set(field(PointTransaction::getPoint), point3)
                        .set(field(PointTransaction::getAmount), BigDecimal.valueOf(15))
                        .create()
        );
        List<PointTransactionSummary> transactionSummaries = List.of(
                Instancio.of(PointTransactionSummary.class)
                        .set(field(PointTransactionSummary::getAction), PointAction.USE)
                        .set(field(PointTransactionSummary::getAmount), BigDecimal.valueOf(20))
                        .set(field(PointTransactionSummary::getTransactions), transactions)
                        .create()
        );

        PointUseCancelExecutor executor = new PointUseCancelExecutor(transactionSummaries);


        List<PointUseCancelResults.Result> expected = List.of(
                PointUseCancelResults.Result.builder()
                        .beforeBalance(BigDecimal.valueOf(8))
                        .canceledAmount(BigDecimal.valueOf(2))
                        .point(point1.withBalance(BigDecimal.TEN))
                        .build(),
                PointUseCancelResults.Result.builder()
                        .beforeBalance(BigDecimal.ZERO)
                        .canceledAmount(BigDecimal.valueOf(3))
                        .point(
                                PointEarnCommand
                                        .builder()
                                        .userId(command.getUserId())
                                        .amount(BigDecimal.valueOf(3))
                                        .build()
                                        .createPoint()
                        )
                        .build(),
                PointUseCancelResults.Result.builder()
                        .beforeBalance(BigDecimal.valueOf(5))
                        .canceledAmount(BigDecimal.valueOf(5))
                        .point(point3.withBalance(BigDecimal.valueOf(10)))
                        .build()
        );
        List<PointUseCancelResults.Result> actual = executor.execute(command);

        assertEquals(expected, actual);
    }
}
