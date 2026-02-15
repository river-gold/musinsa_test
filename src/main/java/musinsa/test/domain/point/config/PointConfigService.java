package musinsa.test.domain.point.config;

import lombok.RequiredArgsConstructor;
import musinsa.test.entity.point.repo.PointConfigRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointConfigService {
    private final PointConfigRepository pointConfigRepository;

    public PointConfig getPointConfig() {
        return PointConfig.of(pointConfigRepository.findFirstBy().orElseThrow());
    }
}
