package musinsa.test.domain.point.transaction;

import musinsa.test.domain.point.code.PointReference;
import musinsa.test.entity.point.PointTransactionSummaryEntity;
import musinsa.test.entity.point.repo.PointTransactionSummaryQuerydslRepository;
import musinsa.test.entity.point.repo.PointTransactionSummaryRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PointTransactionSummaryServiceTest {
    @Mock
    private PointTransactionSummaryRepository pointTransactionSummaryRepository;
    @Mock
    private PointTransactionSummaryQuerydslRepository pointTransactionSummaryQuerydslRepository;
    @Mock
    private PointTransactionService pointTransactionService;

    @InjectMocks
    private PointTransactionSummaryService pointTransactionSummaryService;

    @Test
    void createTest() {
        List<PointTransaction> transactions = Instancio.createList(PointTransaction.class);
        PointTransactionSummary transactionSummary = Instancio.of(PointTransactionSummary.class)
                .set(field(PointTransactionSummary::getTransactions), transactions)
                .create();
        PointTransactionSummaryEntity entity = transactionSummary.createEntity();

        given(pointTransactionSummaryRepository.save(entity)).willReturn(entity);
        given(pointTransactionService.createAll(entity.getId(), transactionSummary.getTransactions())).willReturn(transactions);

        PointTransactionSummary expected = PointTransactionSummary.of(entity, transactions);
        PointTransactionSummary actual = pointTransactionSummaryService.create(transactionSummary);

        assertEquals(expected, actual);
    }

    @Test
    void getByReferenceTest() {
        Long userId = Instancio.create(Long.class);
        PointReference reference = Instancio.create(PointReference.class);
        String referenceKey = Instancio.create(String.class);
        List<PointTransactionSummaryEntity> entities = Instancio.createList(PointTransactionSummaryEntity.class);
        List<PointTransaction> transactions = Instancio.createList(PointTransaction.class);
        List<Long> summaryIds = entities.stream().map(PointTransactionSummaryEntity::getId).toList();

        given(pointTransactionSummaryQuerydslRepository.findByReference(userId, reference, referenceKey)).willReturn(entities);
        given(pointTransactionService.getAll(summaryIds)).willReturn(transactions);

        List<PointTransactionSummary> expected = PointTransactionSummary.list(entities, transactions);
        List<PointTransactionSummary> actual = pointTransactionSummaryService.getByReference(userId, reference, referenceKey);

        assertEquals(expected, actual);
    }
}
