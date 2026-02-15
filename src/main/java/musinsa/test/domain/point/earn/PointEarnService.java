package musinsa.test.domain.point.earn;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musinsa.test.aspect.DistributedLock;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.transaction.PointTransactionSummaryService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointEarnService {
    private final PointEarnExecuteService pointEarnExecuteService;
    private final PointTransactionSummaryService pointTransactionSummaryService;

    @DistributedLock(key = "'user:point:' + #command.userId")
    @Transactional
    public Point earn(PointEarnCommand command) {
        PointEarnResult result = pointEarnExecuteService.earn(command);
        pointTransactionSummaryService.create(command.createTransaction(result));
        log.info("포인트 생성 성공: {}", result.getPoint());
        return result.getPoint();
    }

    @DistributedLock(key = "'user:point:' + #command.userId")
    @Transactional
    public Point cancel(PointEarnCancelCommand command) {
        PointEarnCancelResult result = pointEarnExecuteService.cancel(command);
        pointTransactionSummaryService.create(command.createTransaction(result));
        log.info("포인트 취소 성공: {}", result.getPoint());
        return result.getPoint();
    }
}
