package musinsa.test.domain.point.earn;

import musinsa.test.domain.point.config.PointConfig;
import musinsa.test.domain.point.config.PointUserConfig;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.instancio.Select.field;

public class PointEarnValidatorTest {

    @Test
    void validateTest() {
        // 만료일이 있으면 만료일이 최대 5년 미만인지 확인한다.
        {
            InstancioApi<PointEarnCommand> commandCreator = Instancio.of(PointEarnCommand.class)
                    .set(field(PointEarnCommand::getAmount), BigDecimal.TEN);

            PointConfig pointConfig = Instancio.of(PointConfig.class)
                    .set(field(PointConfig::maxEarnAmount), BigDecimal.valueOf(500))
                    .create();

            PointEarnValidator executor = new PointEarnValidator(
                    pointConfig,
                    Optional.empty(),
                    BigDecimal.ZERO,
                    Optional.empty()
            );

            Assertions.assertDoesNotThrow(() -> {
                PointEarnCommand command = commandCreator.set(field(PointEarnCommand::getExpireDays), Optional.of(365 * 5 - 1)).create();
                executor.validate(command);
            });

            Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
                PointEarnCommand command = commandCreator.set(field(PointEarnCommand::getExpireDays), Optional.of(365 * 5)).create();
                executor.validate(command);
            });
        }

        // 충전 포인트는 0보다 커야한다.
        {
            InstancioApi<PointEarnCommand> commandCreator = Instancio.of(PointEarnCommand.class)
                    .set(field(PointEarnCommand::getExpireDays), Optional.empty());

            PointConfig pointConfig = Instancio.of(PointConfig.class)
                    .set(field(PointConfig::maxEarnAmount), BigDecimal.valueOf(500))
                    .create();

            PointEarnValidator executor = new PointEarnValidator(
                    pointConfig,
                    Optional.empty(),
                    BigDecimal.ZERO,
                    Optional.empty()
            );

            Assertions.assertDoesNotThrow(() -> {
                PointEarnCommand command = commandCreator.set(field(PointEarnCommand::getAmount), BigDecimal.ONE).create();
                executor.validate(command);
            });

            Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
                PointEarnCommand command = commandCreator.set(field(PointEarnCommand::getAmount), BigDecimal.ZERO).create();
                executor.validate(command);
            });
        }

        // 충전 포인트는 PointConfig.getMaxEarnAmount 이하이어야 한다.
        {
            InstancioApi<PointEarnCommand> commandCreator = Instancio.of(PointEarnCommand.class)
                    .set(field(PointEarnCommand::getExpireDays), Optional.empty());

            PointConfig pointConfig = Instancio.of(PointConfig.class)
                    .set(field(PointConfig::maxEarnAmount), BigDecimal.valueOf(500))
                    .create();

            PointEarnValidator executor = new PointEarnValidator(
                    pointConfig,
                    Optional.empty(),
                    BigDecimal.ZERO,
                    Optional.empty()
            );

            Assertions.assertDoesNotThrow(() -> {
                PointEarnCommand command = commandCreator.set(field(PointEarnCommand::getAmount), BigDecimal.valueOf(500)).create();
                executor.validate(command);
            });

            Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
                PointEarnCommand command = commandCreator.set(field(PointEarnCommand::getAmount), BigDecimal.valueOf(501)).create();
                executor.validate(command);
            });
        }

        // pointUserConfig가 존재하면 충전 후 잔액이 pointUserConfig.maxBalance 이하이어야 한다.
        {
            PointEarnCommand command = Instancio.of(PointEarnCommand.class)
                    .set(field(PointEarnCommand::getAmount), BigDecimal.valueOf(500))
                    .set(field(PointEarnCommand::getExpireDays), Optional.empty())
                    .create();

            PointConfig pointConfig = Instancio.of(PointConfig.class)
                    .set(field(PointConfig::maxEarnAmount), BigDecimal.valueOf(500))
                    .create();

            Assertions.assertDoesNotThrow(() -> {
                PointUserConfig userConfig = Instancio.of(PointUserConfig.class)
                        .set(field(PointUserConfig::maxBalance), BigDecimal.valueOf(1000))
                        .create();
                PointEarnValidator executor = new PointEarnValidator(
                        pointConfig,
                        Optional.of(userConfig),
                        BigDecimal.valueOf(500),
                        Optional.empty()
                );

                executor.validate(command);
            });

            Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
                PointUserConfig userConfig = Instancio.of(PointUserConfig.class)
                        .set(field(PointUserConfig::maxBalance), BigDecimal.valueOf(1000))
                        .create();
                PointEarnValidator executor = new PointEarnValidator(
                        pointConfig,
                        Optional.of(userConfig),
                        BigDecimal.valueOf(501),
                        Optional.empty()
                );

                executor.validate(command);
            });
        }

        // 포인트가 존재하면 에러 발생
        {
            PointEarnCommand command = Instancio.of(PointEarnCommand.class)
                    .set(field(PointEarnCommand::getAmount), BigDecimal.valueOf(500))
                    .set(field(PointEarnCommand::getExpireDays), Optional.empty())
                    .create();

            PointConfig pointConfig = Instancio.of(PointConfig.class)
                    .set(field(PointConfig::maxEarnAmount), BigDecimal.valueOf(500))
                    .create();

            Assertions.assertThrowsExactly(IllegalStateException.class, () -> {
                PointEarnValidator executor = new PointEarnValidator(
                        pointConfig,
                        Optional.empty(),
                        BigDecimal.ZERO,
                        Optional.of(Mockito.mock())
                );
                executor.validate(command);
            });
        }

        // 포인트 정상 발급
        {
            PointEarnCommand command = Instancio.of(PointEarnCommand.class)
                    .set(field(PointEarnCommand::getAmount), BigDecimal.valueOf(500))
                    .set(field(PointEarnCommand::getExpireDays), Optional.empty())
                    .create();

            PointConfig pointConfig = Instancio.of(PointConfig.class)
                    .set(field(PointConfig::maxEarnAmount), BigDecimal.valueOf(500))
                    .create();

            PointEarnValidator executor = new PointEarnValidator(
                    pointConfig,
                    Optional.empty(),
                    BigDecimal.ZERO,
                    Optional.empty()
            );

            Assertions.assertDoesNotThrow(() -> executor.validate(command));
        }
    }
}
