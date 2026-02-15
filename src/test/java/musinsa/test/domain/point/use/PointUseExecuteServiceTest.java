package musinsa.test.domain.point.use;

import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.PointService;
import musinsa.test.domain.point.transaction.PointTransactionSummary;
import musinsa.test.domain.point.transaction.PointTransactionSummaryService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PointUseExecuteServiceTest {
    @Mock
    private PointService pointService;
    @Mock
    private PointTransactionSummaryService pointTransactionSummaryService;
    @Mock
    private PointUseExecutor.Factory pointUseExecutorFactory;
    @Mock
    private PointUseCancelExecutor.Factory pointUseCancelExecutorFactory;

    @InjectMocks
    private PointUseExecuteService pointUseExecuteService;

    @Test
    void useTest() {
        PointUseCommand command = Instancio.create(PointUseCommand.class);
        PointUseExecutor executor = Mockito.mock();
        List<Point> points = Instancio.createList(Point.class);
        List<PointUseResults.Result> resultList = Instancio.createList(PointUseResults.Result.class);

        given(pointService.getPointsByUserWithLock(command.getUserId())).willReturn(points);
        given(pointUseExecutorFactory.create(points)).willReturn(executor);
        given(executor.execute(command)).willReturn(resultList);

        PointUseResults expected = PointUseResults
                .builder()
                .results(resultList)
                .beforeSumBalance(Point.sumBalance(points))
                .usedAmount(command.getAmount())
                .build();
        PointUseResults actual = pointUseExecuteService.use(command);

        assertEquals(expected, actual);
    }

    @Test
    void cancelTest() {
        PointUseCancelCommand command = Instancio.create(PointUseCancelCommand.class);
        PointUseCancelExecutor executor = Mockito.mock();
        List<Point> points = Instancio.createList(Point.class);
        Point point = Instancio.create(Point.class);
        List<PointTransactionSummary> transactions = Instancio.createList(PointTransactionSummary.class);
        List<PointUseCancelResults.Result> resultList = Instancio.createList(PointUseCancelResults.Result.class);

        given(pointService.getPointsByUserWithLock(command.getUserId())).willReturn(points);
        given(pointTransactionSummaryService.getByReference(command.getUserId(), command.getReference(), command.getReferenceKey())).willReturn(transactions);
        given(pointUseCancelExecutorFactory.create(transactions)).willReturn(executor);
        given(pointService.save(any())).willReturn(point);
        given(executor.execute(command)).willReturn(resultList);

        PointUseCancelResults expected = PointUseCancelResults
                .builder()
                .results(resultList.stream().map(r -> r.withPoint(point)).toList())
                .beforeSumBalance(Point.sumBalance(points))
                .canceledAmount(command.getAmount())
                .build();
        PointUseCancelResults actual = pointUseExecuteService.cancel(command);

        assertEquals(expected, actual);
    }
}
