package musinsa.test.domain.point.config;

import musinsa.test.entity.point.PointConfigEntity;
import musinsa.test.entity.point.repo.PointConfigRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PointConfigServiceTest {

    @Mock
    private PointConfigRepository pointConfigRepository;

    @InjectMocks
    private PointConfigService pointConfigService;

    @Test
    void getPointUserConfigTest() {
        {
            PointConfigEntity entity = Instancio.create(PointConfigEntity.class);
            given(pointConfigRepository.findFirstBy()).willReturn(Optional.of(entity));

            PointConfig expected = PointConfig.of(entity);
            PointConfig actual = pointConfigService.getPointConfig();

            assertEquals(expected, actual);
        }
        {
            given(pointConfigRepository.findFirstBy()).willReturn(Optional.empty());
            assertThrowsExactly(NoSuchElementException.class, () -> pointConfigService.getPointConfig());
        }
    }
}
