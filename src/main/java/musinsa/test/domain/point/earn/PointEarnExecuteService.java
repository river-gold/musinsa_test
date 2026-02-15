package musinsa.test.domain.point.earn;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.PointService;
import musinsa.test.domain.point.config.PointConfig;
import musinsa.test.domain.point.config.PointConfigService;
import musinsa.test.domain.point.config.PointUserConfig;
import musinsa.test.domain.point.config.PointUserConfigService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointEarnExecuteService {
    private final PointConfigService pointConfigService;
    private final PointUserConfigService pointUserConfigService;
    private final PointService pointService;

    @Transactional
    public PointEarnResult earn(PointEarnCommand command) {
        List<Point> points = pointService.getPointsByUserWithLock(command.getUserId());
        BigDecimal sumBalance = Point.sumBalance(points);
        PointConfig pointConfig = pointConfigService.getPointConfig();
        Optional<PointUserConfig> pointUserConfig = pointUserConfigService.getPointUserConfig(command.getUserId());
        Optional<Point> pointOpt = pointService.getPointByKeyWithLock(command.getPointKey());

        PointEarnValidator validator = PointEarnValidator.builder()
                .sumBalance(sumBalance)
                .pointConfig(pointConfig)
                .pointUserConfig(pointUserConfig)
                .point(pointOpt)
                .build();

        validator.validate(command);

        Point point = pointService.create(command.createPoint());

        return PointEarnResult.builder()
                .beforeBalance(Point.sumBalance(points))
                .earnedAmount(command.getAmount())
                .point(point)
                .build();
    }

    @Transactional
    public PointEarnCancelResult cancel(PointEarnCancelCommand command) {
        List<Point> points = pointService.getPointsByUserWithLock(command.userId());
        Optional<Point> pointOpt = pointService.getPointByKeyWithLock(command.pointKey());

        PointEarnCancelValidator validator = PointEarnCancelValidator.builder()
                .point(pointOpt)
                .build();

        validator.validate(command);

        Point point = pointService.update(pointOpt.orElseThrow().cancel());

        return PointEarnCancelResult.builder()
                .beforeBalance(Point.sumBalance(points))
                .canceledAmount(point.earnedAmount())
                .point(point)
                .build();
    }
}
