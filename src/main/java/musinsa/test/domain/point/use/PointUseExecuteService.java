package musinsa.test.domain.point.use;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.PointService;
import musinsa.test.domain.point.transaction.PointTransactionSummary;
import musinsa.test.domain.point.transaction.PointTransactionSummaryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointUseExecuteService {
    private final PointService pointService;
    private final PointTransactionSummaryService pointTransactionSummaryService;
    private final PointUseExecutor.Factory pointUseExecutorFactory;
    private final PointUseCancelExecutor.Factory pointUseCancelExecutorFactory;

    @Transactional
    public PointUseResults use(PointUseCommand command) {
        List<Point> points = pointService.getPointsByUserWithLock(command.getUserId());

        PointUseExecutor executor = pointUseExecutorFactory.create(points);
        List<PointUseResults.Result> resultList = executor.execute(command);

        pointService.updateAll(resultList.stream().map(PointUseResults.Result::getPoint).toList());

        return PointUseResults
                .builder()
                .results(resultList)
                .beforeSumBalance(Point.sumBalance(points))
                .usedAmount(command.getAmount())
                .build();
    }

    @Transactional
    public PointUseCancelResults cancel(PointUseCancelCommand command) {
        List<Point> points = pointService.getPointsByUserWithLock(command.getUserId());
        List<PointTransactionSummary> transactionSummaries = pointTransactionSummaryService.getByReference(command.getUserId(), command.getReference(), command.getReferenceKey());

        PointUseCancelExecutor executor = pointUseCancelExecutorFactory.create(transactionSummaries);
        List<PointUseCancelResults.Result> resultList = executor.execute(command)
                .stream()
                .map(r -> r.withPoint(pointService.save(r.getPoint())))
                .toList();

        return PointUseCancelResults.builder()
                .results(resultList)
                .beforeSumBalance(Point.sumBalance(points))
                .canceledAmount(command.getAmount())
                .build();
    }
}
