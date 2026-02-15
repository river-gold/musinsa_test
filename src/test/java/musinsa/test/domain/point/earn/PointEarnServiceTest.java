package musinsa.test.domain.point.earn;

import musinsa.test.domain.point.Point;
import musinsa.test.domain.point.transaction.PointTransactionSummaryService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PointEarnServiceTest {
    @Mock
    private PointEarnExecuteService pointEarnExecuteService;
    @Mock
    private PointTransactionSummaryService pointTransactionSummaryService;

    @InjectMocks
    private PointEarnService pointEarnService;

    @Test
    void earnTest() {
        PointEarnCommand command = Instancio.create(PointEarnCommand.class);
        PointEarnResult result = Instancio.create(PointEarnResult.class);

        given(pointEarnExecuteService.earn(command)).willReturn(result);

        Point expected = result.getPoint();
        Point actual = pointEarnService.earn(command);

        assertEquals(expected, actual);

        verify(pointTransactionSummaryService, times(1)).create(command.createTransaction(result));
    }

    @Test
    void cancelTest() {
        PointEarnCancelCommand command = Instancio.create(PointEarnCancelCommand.class);
        PointEarnCancelResult result = Instancio.create(PointEarnCancelResult.class);

        given(pointEarnExecuteService.cancel(command)).willReturn(result);

        Point expected = result.getPoint();
        Point actual = pointEarnService.cancel(command);

        assertEquals(expected, actual);

        verify(pointTransactionSummaryService, times(1)).create(command.createTransaction(result));
    }
}
