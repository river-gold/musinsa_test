package musinsa.test.domain.point.config;

import musinsa.test.entity.point.PointUserConfigEntity;
import musinsa.test.entity.point.repo.PointUserConfigRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PointUserConfigServiceTest {

    @Mock
    private PointUserConfigRepository pointUserConfigRepository;

    @InjectMocks
    private PointUserConfigService pointUserConfigService;

    @Test
    void getPointUserConfigTest() {
        PointUserConfigEntity entity = Instancio.create(PointUserConfigEntity.class);
        given(pointUserConfigRepository.findByUserId(123L)).willReturn(Optional.of(entity));

        Optional<PointUserConfig> expected = Optional.of(PointUserConfig.of(entity));
        Optional<PointUserConfig> actual = pointUserConfigService.getPointUserConfig(123L);

        assertEquals(expected, actual);
    }
}
