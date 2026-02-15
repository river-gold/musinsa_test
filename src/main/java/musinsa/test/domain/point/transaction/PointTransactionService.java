package musinsa.test.domain.point.transaction;

import lombok.RequiredArgsConstructor;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.PointService;
import musinsa.test.entity.point.PointTransactionEntity;
import musinsa.test.entity.point.repo.PointTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointTransactionService {
    private final PointTransactionRepository pointTransactionRepository;
    private final PointService pointService;

    public List<PointTransaction> createAll(Long summaryId, List<PointTransaction> transactions) {
        List<PointTransactionEntity> entities = pointTransactionRepository.saveAll(transactions.stream().map(d -> d.createEntity(summaryId)).toList());
        List<Point> points = pointService.getPoints(entities.stream().map(PointTransactionEntity::getPointId).toList());
        return PointTransaction.list(entities, points);
    }

    public List<PointTransaction> getAll(List<Long> summaryIds) {
        List<PointTransactionEntity> entities = pointTransactionRepository.findAllBySummaryIdIn(summaryIds);
        List<Point> points = pointService.getPoints(entities.stream().map(PointTransactionEntity::getPointId).toList());
        return PointTransaction.list(entities, points);
    }
}
