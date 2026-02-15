package musinsa.test.domain.point.use;

import musinsa.test.domain.point.transaction.PointTransactionSummary;
import musinsa.test.domain.point.transaction.PointTransactionSummaryService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class PointUseServiceTest {
    @Mock
    private PointTransactionSummaryService pointTransactionSummaryService;
    @Mock
    private PointUseExecuteService pointUseExecuteService;

    @InjectMocks
    private PointUseService pointUseService;

    @Test
    void useTest() {
        PointUseCommand command = Instancio.create(PointUseCommand.class);
        PointUseResults results = Instancio.create(PointUseResults.class);
        PointTransactionSummary transactionSummary = Instancio.create(PointTransactionSummary.class);

        given(pointUseExecuteService.use(command)).willReturn(results);
        given(pointTransactionSummaryService.create(command.createTransaction(results))).willReturn(transactionSummary);

        PointTransactionSummary actual = pointUseService.use(command);

        assertEquals(transactionSummary, actual);
    }

    @Test
    void cancelTest() {
        PointUseCancelCommand command = Instancio.create(PointUseCancelCommand.class);
        PointUseCancelResults results = Instancio.create(PointUseCancelResults.class);
        PointTransactionSummary transactionSummary = Instancio.create(PointTransactionSummary.class);

        given(pointUseExecuteService.cancel(command)).willReturn(results);
        given(pointTransactionSummaryService.create(command.createTransaction(results))).willReturn(transactionSummary);

        PointTransactionSummary actual = pointUseService.cancel(command);

        assertEquals(transactionSummary, actual);
    }
}
