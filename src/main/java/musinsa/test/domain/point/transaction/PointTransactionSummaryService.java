package musinsa.test.domain.point.transaction;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import musinsa.test.domain.point.code.PointReference;
import musinsa.test.entity.point.PointTransactionSummaryEntity;
import musinsa.test.entity.point.repo.PointTransactionSummaryQuerydslRepository;
import musinsa.test.entity.point.repo.PointTransactionSummaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointTransactionSummaryService {
    private final PointTransactionSummaryRepository pointTransactionSummaryRepository;
    private final PointTransactionSummaryQuerydslRepository pointTransactionSummaryQuerydslRepository;
    private final PointTransactionService pointTransactionService;

    @Transactional
    public PointTransactionSummary create(PointTransactionSummary transactionSummary) {
        PointTransactionSummaryEntity entity = pointTransactionSummaryRepository.save(transactionSummary.createEntity());
        List<PointTransaction> transactions = pointTransactionService.createAll(entity.getId(), transactionSummary.getTransactions());
        return PointTransactionSummary.of(entity, transactions);
    }

    @Transactional
    public List<PointTransactionSummary> getByReference(
            Long userId,
            PointReference reference,
            String referenceKey
    ) {
        List<PointTransactionSummaryEntity> entities = pointTransactionSummaryQuerydslRepository.findByReference(userId, reference, referenceKey);
        List<Long> summaryIds = entities.stream().map(PointTransactionSummaryEntity::getId).toList();
        List<PointTransaction> transactions = pointTransactionService.getAll(summaryIds);
        return PointTransactionSummary.list(entities, transactions);
    }
}
