package musinsa.test.entity.point.repo;

import musinsa.test.entity.point.PointTransactionEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class PointTransactionRepositoryTest {

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Test
    void findFirstByTest() {
        List<PointTransactionEntity> expected = List.of(
                Instancio.of(PointTransactionEntity.class)
                        .ignore(field(PointTransactionEntity::getId))
                        .set(field(PointTransactionEntity::getSummaryId), 1L)
                        .create(),
                Instancio.of(PointTransactionEntity.class)
                        .ignore(field(PointTransactionEntity::getId))
                        .set(field(PointTransactionEntity::getSummaryId), 1L)
                        .create()
        );

        pointTransactionRepository.saveAll(expected);

        List<PointTransactionEntity> actual = pointTransactionRepository.findAllBySummaryIdIn(List.of(1L));

        assertEquals(expected, actual);
    }
}
