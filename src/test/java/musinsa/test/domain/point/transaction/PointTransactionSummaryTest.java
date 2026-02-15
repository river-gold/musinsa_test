package musinsa.test.domain.point.transaction;

import musinsa.test.entity.point.PointTransactionSummaryEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointTransactionSummaryTest {

    @Test
    void listTest() {
        List<PointTransactionSummaryEntity> entities = List.of(
                Instancio.of(PointTransactionSummaryEntity.class).set(field(PointTransactionSummaryEntity::getId), 1L).create(),
                Instancio.of(PointTransactionSummaryEntity.class).set(field(PointTransactionSummaryEntity::getId), 2L).create(),
                Instancio.of(PointTransactionSummaryEntity.class).set(field(PointTransactionSummaryEntity::getId), 3L).create()
        );
        List<PointTransaction> transactions = List.of(
                Instancio.of(PointTransaction.class).set(field(PointTransaction::getSummaryId), 1L).create(),
                Instancio.of(PointTransaction.class).set(field(PointTransaction::getSummaryId), 1L).create(),
                Instancio.of(PointTransaction.class).set(field(PointTransaction::getSummaryId), 2L).create(),
                Instancio.of(PointTransaction.class).set(field(PointTransaction::getSummaryId), 3L).create(),
                Instancio.of(PointTransaction.class).set(field(PointTransaction::getSummaryId), 4L).create()
        );

        List<PointTransactionSummary> expected = List.of(
                PointTransactionSummary.of(entities.get(0), List.of(transactions.get(0), transactions.get(1))),
                PointTransactionSummary.of(entities.get(1), List.of(transactions.get(2))),
                PointTransactionSummary.of(entities.get(2), List.of(transactions.get(3)))
        );
        List<PointTransactionSummary> actual = PointTransactionSummary.list(entities, transactions);

        assertEquals(expected, actual);
    }
}
