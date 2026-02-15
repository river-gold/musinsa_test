package musinsa.test.entity.point.repo;

import jakarta.transaction.Transactional;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.entity.point.PointEntity;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(InstancioExtension.class)
public class PointQuerydslRepositoryTest {
    private final InstancioApi<PointEntity> entityCreator = Instancio.of(PointEntity.class)
            .ignore(field(PointEntity::getId))
            .set(field(PointEntity::getUserId), 123L)
            .set(field(PointEntity::getStatus), PointStatus.EARNED);
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private PointQuerydslRepository pointQuerydslRepository;

    @Test
    @Transactional
    void findByKeyWithLockTest() {
        PointEntity expected = entityCreator.create();

        pointRepository.save(expected);

        PointEntity actual = pointQuerydslRepository.findByKeyWithLock(expected.getPointKey());

        assertEquals(expected, actual);
    }

    @Test
    @Transactional
    @DisplayName("지급상태이고 만료일이 유효한 포인트만 조회")
    void findAllByUserWithLockTest() {
        List<PointEntity> expected = List.of(
                entityCreator
                        .set(field(PointEntity::getExpireDate), LocalDate.now())
                        .create(),
                entityCreator
                        .set(field(PointEntity::getExpireDate), LocalDate.now().plusDays(1))
                        .create()
        );

        pointRepository.saveAll(expected);
        pointRepository.save(
                entityCreator
                        .set(field(PointEntity::getExpireDate), LocalDate.now().minusDays(1))
                        .create()
        );

        List<PointEntity> actual = pointQuerydslRepository.findAllByUserWithLock(123L);

        assertEquals(expected, actual);
    }

    @Test
    @Transactional
    @DisplayName("ID로 포인트 조회")
    void findAllTest() {
        List<PointEntity> expected = List.of(
                entityCreator.create(),
                entityCreator.create()
        );

        pointRepository.saveAll(expected);
        pointRepository.save(entityCreator.create());

        List<PointEntity> actual = pointQuerydslRepository.findAll(expected.stream().map(PointEntity::getId).toList());

        assertEquals(expected, actual);
    }
}
