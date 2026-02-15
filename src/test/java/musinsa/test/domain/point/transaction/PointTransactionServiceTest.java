package musinsa.test.domain.point.transaction;

import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.PointService;
import musinsa.test.entity.point.PointTransactionEntity;
import musinsa.test.entity.point.repo.PointTransactionRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PointTransactionServiceTest {
    @Mock
    private PointTransactionRepository pointTransactionRepository;
    @Mock
    private PointService pointService;

    @InjectMocks
    private PointTransactionService pointTransactionService;

    @Test
    void createAllTest() {
        Long transactionId = Instancio.create(Long.class);
        List<PointTransaction> transactions = Instancio.createList(PointTransaction.class);
        List<PointTransactionEntity> entities = transactions.stream().map(pointTransaction -> pointTransaction.createEntity(transactionId)).toList();
        List<Long> pointIds = entities.stream().map(PointTransactionEntity::getPointId).toList();
        List<Point> points = Instancio.createList(Point.class);

        given(pointTransactionRepository.saveAll(entities)).willReturn(entities);
        given(pointService.getPoints(pointIds)).willReturn(points);

        List<PointTransaction> expected = PointTransaction.list(entities, points);
        List<PointTransaction> actual = pointTransactionService.createAll(transactionId, transactions);

        assertEquals(expected, actual);
    }

    @Test
    void getAllTest() {
        List<Long> transactionIds = Instancio.createList(Long.class);
        List<PointTransactionEntity> entities = Instancio.createList(PointTransactionEntity.class);
        List<Long> pointIds = entities.stream().map(PointTransactionEntity::getPointId).toList();
        List<Point> points = Instancio.createList(Point.class);

        given(pointTransactionRepository.findAllBySummaryIdIn(transactionIds)).willReturn(entities);
        given(pointService.getPoints(pointIds)).willReturn(points);

        List<PointTransaction> expected = PointTransaction.list(entities, points);
        List<PointTransaction> actual = pointTransactionService.getAll(transactionIds);

        assertEquals(expected, actual);
    }
}
