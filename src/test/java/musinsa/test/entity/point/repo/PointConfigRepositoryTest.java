package musinsa.test.entity.point.repo;

import musinsa.test.entity.point.PointConfigEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class PointConfigRepositoryTest {

    @Autowired
    private PointConfigRepository pointConfigRepository;

    @Test
    void findFirstByTest() {
        List<PointConfigEntity> pointConfigs = List.of(
                pointConfigRepository.save(PointConfigEntity.builder().maxEarnAmount(BigDecimal.ONE).build()),
                pointConfigRepository.save(PointConfigEntity.builder().maxEarnAmount(BigDecimal.TEN).build())
        );

        assertEquals(Optional.of(pointConfigs.getFirst()), pointConfigRepository.findFirstBy());
    }
}
