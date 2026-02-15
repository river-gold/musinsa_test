package musinsa.test.domain.point.use;

import musinsa.test.domain.point.earn.PointEarnExecuteService;
import musinsa.test.domain.point.transaction.PointTransactionSummaryService;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class PointUseServiceAspectTest {
    @MockitoBean
    private PointEarnExecuteService pointEarnExecuteService;
    @MockitoBean
    private PointTransactionSummaryService pointTransactionSummaryService;
    @MockitoBean
    private RedissonClient redissonClient;

    @Autowired
    private PointUseService pointUseService;

    @Test
    @DisplayName("분산락 테스트")
    void useLockTest() {
        PointUseCommand command = Instancio.create(PointUseCommand.class);
        String lockKey = "user:point:%d".formatted(command.getUserId());

        given(redissonClient.getLock(lockKey)).willReturn(Mockito.mock());

        try {
            pointUseService.use(command);
        } catch (Exception e) {

        }
        verify(redissonClient, times(1)).getLock(lockKey);
    }

    @Test
    @DisplayName("분산락 테스트")
    void cancelLockTest() {
        PointUseCancelCommand command = Instancio.create(PointUseCancelCommand.class);
        String lockKey = "user:point:%d".formatted(command.getUserId());

        given(redissonClient.getLock(lockKey)).willReturn(Mockito.mock());

        try {
            pointUseService.cancel(command);
        } catch (Exception e) {

        }
        verify(redissonClient, times(1)).getLock(lockKey);
    }
}
