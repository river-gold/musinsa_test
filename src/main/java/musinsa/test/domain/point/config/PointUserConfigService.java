package musinsa.test.domain.point.config;

import lombok.RequiredArgsConstructor;
import musinsa.test.entity.point.repo.PointUserConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointUserConfigService {
    private final PointUserConfigRepository pointUserConfigRepository;

    public Optional<PointUserConfig> getPointUserConfig(Long userId) {
        return pointUserConfigRepository.findByUserId(userId).map(PointUserConfig::of);
    }
}
