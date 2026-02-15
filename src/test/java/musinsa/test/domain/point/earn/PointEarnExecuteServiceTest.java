package musinsa.test.domain.point.earn;

import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.PointService;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.domain.point.config.PointConfig;
import musinsa.test.domain.point.config.PointConfigService;
import musinsa.test.domain.point.config.PointUserConfig;
import musinsa.test.domain.point.config.PointUserConfigService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PointEarnExecuteServiceTest {
    @Mock
    private PointConfigService pointConfigService;
    @Mock
    private PointUserConfigService pointUserConfigService;
    @Mock
    private PointService pointService;
    @InjectMocks
    private PointEarnExecuteService pointEarnExecuteService;

    @Test
    void getEarnValidatorTest() {
        PointEarnCommand command = Instancio.of(PointEarnCommand.class)
                .set(field(PointEarnCommand::getAmount), BigDecimal.valueOf(500))
                .set(field(PointEarnCommand::getExpireDays), Optional.empty())
                .create();
        PointConfig pointConfig = Instancio.of(PointConfig.class)
                .set(field(PointConfig::maxEarnAmount), BigDecimal.valueOf(500))
                .create();
        Optional<PointUserConfig> pointUserConfig = Optional.empty();
        List<Point> points = List.of();
        Optional<Point> pointOpt = Optional.empty();

        given(pointConfigService.getPointConfig()).willReturn(pointConfig);
        given(pointUserConfigService.getPointUserConfig(command.getUserId())).willReturn(pointUserConfig);
        given(pointService.getPointsByUserWithLock(command.getUserId())).willReturn(points);
        given(pointService.getPointByKeyWithLock(command.getPointKey())).willReturn(pointOpt);
        given(pointService.create(command.createPoint())).willReturn(command.createPoint());

        PointEarnResult expected = PointEarnResult.builder()
                .beforeBalance(BigDecimal.ZERO)
                .earnedAmount(command.getAmount())
                .point(command.createPoint())
                .build();

        PointEarnResult actual = pointEarnExecuteService.earn(command);

        assertEquals(expected, actual);
    }

    @Test
    void getCancelValidatorTest() {
        PointEarnCancelCommand command = Instancio.create(PointEarnCancelCommand.class);
        List<Point> points = Instancio.createList(Point.class);
        Point point = Instancio.of(Point.class)
                .set(field(Point::status), PointStatus.EARNED)
                .set(field(Point::expireDate), LocalDate.MAX)
                .set(field(Point::earnedAmount), BigDecimal.TEN)
                .set(field(Point::balance), BigDecimal.TEN)
                .create();

        given(pointService.getPointsByUserWithLock(command.userId())).willReturn(points);
        given(pointService.getPointByKeyWithLock(command.pointKey())).willReturn(Optional.of(point));
        given(pointService.update(point.cancel())).willReturn(point.cancel());

        PointEarnCancelResult expected = PointEarnCancelResult.builder()
                .beforeBalance(Point.sumBalance(points))
                .canceledAmount(point.earnedAmount())
                .point(point.cancel())
                .build();

        PointEarnCancelResult actual = pointEarnExecuteService.cancel(command);

        assertEquals(expected, actual);
    }
}
