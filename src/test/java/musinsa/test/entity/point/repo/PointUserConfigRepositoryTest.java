package musinsa.test.entity.point.repo;

import musinsa.test.entity.point.PointUserConfigEntity;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class PointUserConfigRepositoryTest {

    private final InstancioApi<PointUserConfigEntity> entityCreator = Instancio.of(PointUserConfigEntity.class).ignore(field(PointUserConfigEntity::getId));
    @Autowired
    private PointUserConfigRepository pointUserConfigRepository;

    @Test
    void findFirstByTest() {
        PointUserConfigEntity expected = entityCreator.create();

        pointUserConfigRepository.save(expected);
        pointUserConfigRepository.save(entityCreator.set(field(PointUserConfigEntity::getUserId), expected.getUserId() + 1).create());

        Optional<PointUserConfigEntity> actual = pointUserConfigRepository.findByUserId(expected.getUserId());

        assertEquals(Optional.of(expected), actual);
    }
}
