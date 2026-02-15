package musinsa.test.entity.point.repo;

import jakarta.transaction.Transactional;
import musinsa.test.entity.point.PointTransactionSummaryEntity;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(InstancioExtension.class)
public class PointTransactionSummaryQuerydslRepositoryTest {
    private final InstancioApi<PointTransactionSummaryEntity> entityCreator = Instancio.of(PointTransactionSummaryEntity.class).ignore(field(PointTransactionSummaryEntity::getId));
    @Autowired
    private PointTransactionSummaryRepository pointTransactionSummaryRepository;
    @Autowired
    private PointTransactionSummaryQuerydslRepository pointTransactionSummaryQuerydslRepository;

    @Test
    @Transactional
    @DisplayName("레퍼런스로 포인트 거래내역 조회")
    void findPointsWithLockTest() {
        PointTransactionSummaryEntity expected = entityCreator.create();

        pointTransactionSummaryRepository.save(expected);


        List<PointTransactionSummaryEntity> actual = pointTransactionSummaryQuerydslRepository.findByReference(
                expected.getUserId(),
                expected.getReference(),
                expected.getReferenceKey()
        );

        assertEquals(List.of(expected), actual);
    }
}
