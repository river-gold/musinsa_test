package musinsa.test.domain.point.use;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musinsa.test.aspect.DistributedLock;
import musinsa.test.domain.point.transaction.PointTransactionSummary;
import musinsa.test.domain.point.transaction.PointTransactionSummaryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointUseService {
    private final PointTransactionSummaryService pointTransactionSummaryService;
    private final PointUseExecuteService pointUseExecuteService;

    @DistributedLock(key = "'user:point:' + #command.userId")
    @Transactional
    public PointTransactionSummary use(PointUseCommand command) {
        PointUseResults results = pointUseExecuteService.use(command);
        PointTransactionSummary transactionSummary = pointTransactionSummaryService.create(command.createTransaction(results));
        log.info("포인트 사용 성공: {}", transactionSummary);
        return transactionSummary;
    }

    @DistributedLock(key = "'user:point:' + #command.userId")
    @Transactional
    public PointTransactionSummary cancel(PointUseCancelCommand command) {
        PointUseCancelResults results = pointUseExecuteService.cancel(command);
        PointTransactionSummary transactionSummary = pointTransactionSummaryService.create(command.createTransaction(results));
        log.info("포인트 사용취소 성공: {}", transactionSummary);
        return transactionSummary;
    }
}
