package musinsa.test.domain.point.api.point.earn;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import musinsa.test.api.common.ApiResponse;
import musinsa.test.api.common.ApiStatus;
import musinsa.test.api.point.earn.PointEarnRequest;
import musinsa.test.api.point.earn.PointEarnResponse;
import musinsa.test.api.point.use.PointUseCancelRequest;
import musinsa.test.api.point.use.PointUseCancelResponse;
import musinsa.test.api.point.use.PointUseRequest;
import musinsa.test.api.point.use.PointUseResponse;
import musinsa.test.domain.point.code.PointAction;
import musinsa.test.domain.point.code.PointEarnType;
import musinsa.test.domain.point.code.PointReference;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.entity.point.PointEntity;
import musinsa.test.entity.point.PointTransactionSummaryEntity;
import musinsa.test.entity.point.PointTransactionEntity;
import musinsa.test.entity.point.repo.PointRepository;
import musinsa.test.entity.point.repo.PointTransactionRepository;
import musinsa.test.entity.point.repo.PointTransactionSummaryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PointUseControllerTest {
    private final Long userId = 123L;
    private final String referenceKey = "order_123";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private PointTransactionSummaryRepository pointTransactionSummaryRepository;
    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    private String earn() throws Exception {
        PointEarnRequest request = new PointEarnRequest(
                userId,
                BigDecimal.TEN,
                Optional.empty()
        );
        MvcResult result = mockMvc.perform(post("/api/points/:earn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ApiResponse<PointEarnResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
        });

        PointEarnResponse response = apiResponse.data().orElseThrow();
        return response.pointKey();
    }

    @Test
    void 포인트1건_사용_통합테스트() throws Exception {
        String pointKey = earn();

        PointUseRequest request = new PointUseRequest(
                userId,
                BigDecimal.TWO,
                PointReference.ORDER,
                referenceKey
        );

        MvcResult result = mockMvc.perform(post("/api/points/:use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ApiResponse<PointUseResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(apiResponse.status(), ApiStatus.SUCCESS);
        assertTrue(apiResponse.message().isEmpty());

        PointUseResponse response = apiResponse.data().orElseThrow();
        assertEquals(response.userId(), userId);
        assertEquals(response.beforeBalance().compareTo(BigDecimal.TEN), 0);
        assertEquals(response.amount().compareTo(BigDecimal.TWO.negate()), 0);
        assertEquals(response.balance().compareTo(BigDecimal.valueOf(8)), 0);

        List<PointEntity> points = pointRepository.findAll();
        assertEquals(points.size(), 1);

        PointEntity point = points.getFirst();
        assertEquals(point.getPointKey(), pointKey);
        assertEquals(point.getUserId(), userId);
        assertEquals(point.getEarnedAmount().compareTo(BigDecimal.TEN), 0);
        assertEquals(point.getBalance().compareTo(BigDecimal.valueOf(8)), 0);
        assertEquals(point.getExpireDate(), LocalDate.now().plusDays(365));
        assertEquals(point.getStatus(), PointStatus.EARNED);
        assertEquals(point.getEarnType(), PointEarnType.SYSTEM);
        assertNull(point.getIssuerId());

        List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll();
        assertEquals(transactionSummaries.size(), 2);

        PointTransactionSummaryEntity transactionSummary = transactionSummaries.stream().filter(d -> d.getAction().equals(PointAction.USE)).findFirst().orElseThrow();
        assertEquals(transactionSummary.getAction(), PointAction.USE);
        assertEquals(transactionSummary.getUserId(), userId);
        assertEquals(transactionSummary.getBeforeSumBalance().compareTo(BigDecimal.TEN), 0);
        assertEquals(transactionSummary.getAmount().compareTo(BigDecimal.TWO.negate()), 0);
        assertEquals(transactionSummary.getSumBalance().compareTo(BigDecimal.valueOf(8)), 0);
        assertEquals(transactionSummary.getReference(), PointReference.ORDER);
        assertEquals(transactionSummary.getReferenceKey(), referenceKey);

        List<PointTransactionEntity> transactions = pointTransactionRepository.findAll();
        assertEquals(transactions.size(), 2);

        PointTransactionEntity transaction = transactions.stream().filter(d -> d.getSummaryId().equals(transactionSummary.getId())).findFirst().orElseThrow();
        assertEquals(transaction.getStatus(), PointStatus.EARNED);
        assertEquals(transaction.getBeforeBalance().compareTo(BigDecimal.TEN), 0);
        assertEquals(transaction.getAmount().compareTo(BigDecimal.TWO.negate()), 0);
        assertEquals(transaction.getBalance().compareTo(BigDecimal.valueOf(8)), 0);
    }

    @Test
    void 포인트2건_사용_통합테스트() throws Exception {
        String pointKey1 = earn();
        String pointKey2 = earn();

        PointUseRequest request = new PointUseRequest(
                userId,
                BigDecimal.valueOf(12),
                PointReference.ORDER,
                referenceKey
        );

        MvcResult result = mockMvc.perform(post("/api/points/:use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ApiResponse<PointUseResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(apiResponse.status(), ApiStatus.SUCCESS);
        assertTrue(apiResponse.message().isEmpty());

        PointUseResponse response = apiResponse.data().orElseThrow();
        assertEquals(response.userId(), userId);
        assertEquals(response.beforeBalance().compareTo(BigDecimal.valueOf(20)), 0);
        assertEquals(response.amount().compareTo(BigDecimal.valueOf(12).negate()), 0);
        assertEquals(response.balance().compareTo(BigDecimal.valueOf(8)), 0);

        List<PointEntity> points = pointRepository.findAll().stream().sorted(Comparator.comparing(PointEntity::getId)).toList();
        assertEquals(points.size(), 2);

        {
            PointEntity point = points.get(0);
            assertEquals(point.getPointKey(), pointKey1);
            assertEquals(point.getUserId(), userId);
            assertEquals(point.getEarnedAmount().compareTo(BigDecimal.TEN), 0);
            assertEquals(point.getBalance().compareTo(BigDecimal.ZERO), 0);
            assertEquals(point.getExpireDate(), LocalDate.now().plusDays(365));
            assertEquals(point.getStatus(), PointStatus.EARNED);
            assertEquals(point.getEarnType(), PointEarnType.SYSTEM);
            assertNull(point.getIssuerId());
        }
        {
            PointEntity point = points.get(1);
            assertEquals(point.getPointKey(), pointKey2);
            assertEquals(point.getUserId(), userId);
            assertEquals(point.getEarnedAmount().compareTo(BigDecimal.TEN), 0);
            assertEquals(point.getBalance().compareTo(BigDecimal.valueOf(8)), 0);
            assertEquals(point.getExpireDate(), LocalDate.now().plusDays(365));
            assertEquals(point.getStatus(), PointStatus.EARNED);
            assertEquals(point.getEarnType(), PointEarnType.SYSTEM);
            assertNull(point.getIssuerId());
        }

        List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll().stream().filter(d -> d.getAction().equals(PointAction.USE)).toList();
        assertEquals(transactionSummaries.size(), 1);

        PointTransactionSummaryEntity transactionSummary = transactionSummaries.get(0);
        assertEquals(transactionSummary.getAction(), PointAction.USE);
        assertEquals(transactionSummary.getUserId(), userId);
        assertEquals(transactionSummary.getBeforeSumBalance().compareTo(BigDecimal.valueOf(20)), 0);
        assertEquals(transactionSummary.getAmount().compareTo(BigDecimal.valueOf(12).negate()), 0);
        assertEquals(transactionSummary.getSumBalance().compareTo(BigDecimal.valueOf(8)), 0);
        assertEquals(transactionSummary.getReference(), PointReference.ORDER);
        assertEquals(transactionSummary.getReferenceKey(), referenceKey);

        List<PointTransactionEntity> transactions = pointTransactionRepository.findAll().stream().filter(d -> Objects.equals(d.getSummaryId(), transactionSummary.getId())).toList();
        assertEquals(transactions.size(), 2);

        {
            PointTransactionEntity transaction = transactions.get(0);
            assertEquals(transaction.getStatus(), PointStatus.EXHAUSTED);
            assertEquals(transaction.getBeforeBalance().compareTo(BigDecimal.TEN), 0);
            assertEquals(transaction.getAmount().compareTo(BigDecimal.TEN.negate()), 0);
            assertEquals(transaction.getBalance().compareTo(BigDecimal.ZERO), 0);
        }

        {
            PointTransactionEntity transaction = transactions.get(1);
            assertEquals(transaction.getStatus(), PointStatus.EARNED);
            assertEquals(transaction.getBeforeBalance().compareTo(BigDecimal.TEN), 0);
            assertEquals(transaction.getAmount().compareTo(BigDecimal.TWO.negate()), 0);
            assertEquals(transaction.getBalance().compareTo(BigDecimal.valueOf(8)), 0);
        }
    }

    @Test
    void 포인트_사용_실패통합테스트() throws Exception {
        earn();
        earn();

        PointUseRequest request = new PointUseRequest(
                userId,
                BigDecimal.valueOf(30),
                PointReference.ORDER,
                referenceKey
        );

        MvcResult result = mockMvc.perform(post("/api/points/:use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ApiResponse<PointUseResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(apiResponse.status(), ApiStatus.ERROR);
        assertTrue(apiResponse.message().isPresent());
        assertTrue(apiResponse.data().isEmpty());

        List<PointEntity> points = pointRepository.findAll();
        assertEquals(points.size(), 2);
        List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll();
        assertEquals(transactionSummaries.size(), 2);
        List<PointTransactionEntity> transactions = pointTransactionRepository.findAll();
        assertEquals(transactions.size(), 2);
    }

    @Test
    void 포인트_사용취소_통합테스트() throws Exception {
        String pointKey1 = earn();
        String pointKey2 = earn();

        {
            PointUseRequest request = new PointUseRequest(
                    userId,
                    BigDecimal.valueOf(12),
                    PointReference.ORDER,
                    referenceKey
            );

            mockMvc.perform(post("/api/points/:use")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
        }

        PointUseCancelRequest request = new PointUseCancelRequest(
                userId,
                BigDecimal.valueOf(11),
                PointReference.ORDER,
                referenceKey
        );

        MvcResult result = mockMvc.perform(post("/api/points/:useCancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ApiResponse<PointUseCancelResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(apiResponse.status(), ApiStatus.SUCCESS);
        assertTrue(apiResponse.message().isEmpty());

        PointUseCancelResponse response = apiResponse.data().orElseThrow();
        assertEquals(response.userId(), userId);
        assertEquals(response.beforeBalance().compareTo(BigDecimal.valueOf(8)), 0);
        assertEquals(response.amount().compareTo(BigDecimal.valueOf(11)), 0);
        assertEquals(response.balance().compareTo(BigDecimal.valueOf(19)), 0);

        List<PointEntity> points = pointRepository.findAll().stream().sorted(Comparator.comparing(PointEntity::getId)).toList();
        assertEquals(points.size(), 2);

        {
            PointEntity point = points.get(0);
            assertEquals(point.getPointKey(), pointKey1);
            assertEquals(point.getUserId(), userId);
            assertEquals(point.getEarnedAmount().compareTo(BigDecimal.TEN), 0);
            assertEquals(point.getBalance().compareTo(BigDecimal.TEN), 0);
            assertEquals(point.getExpireDate(), LocalDate.now().plusDays(365));
            assertEquals(point.getStatus(), PointStatus.EARNED);
            assertEquals(point.getEarnType(), PointEarnType.SYSTEM);
            assertNull(point.getIssuerId());
        }
        {
            PointEntity point = points.get(1);
            assertEquals(point.getPointKey(), pointKey2);
            assertEquals(point.getUserId(), userId);
            assertEquals(point.getEarnedAmount().compareTo(BigDecimal.TEN), 0);
            assertEquals(point.getBalance().compareTo(BigDecimal.valueOf(9)), 0);
            assertEquals(point.getExpireDate(), LocalDate.now().plusDays(365));
            assertEquals(point.getStatus(), PointStatus.EARNED);
            assertEquals(point.getEarnType(), PointEarnType.SYSTEM);
            assertNull(point.getIssuerId());
        }

        List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll().stream().filter(d -> d.getAction().equals(PointAction.USE_CANCEL)).toList();
        assertEquals(transactionSummaries.size(), 1);

        PointTransactionSummaryEntity transactionSummary = transactionSummaries.get(0);
        assertEquals(transactionSummary.getAction(), PointAction.USE_CANCEL);
        assertEquals(transactionSummary.getUserId(), userId);
        assertEquals(transactionSummary.getBeforeSumBalance().compareTo(BigDecimal.valueOf(8)), 0);
        assertEquals(transactionSummary.getAmount().compareTo(BigDecimal.valueOf(11)), 0);
        assertEquals(transactionSummary.getSumBalance().compareTo(BigDecimal.valueOf(19)), 0);
        assertEquals(transactionSummary.getReference(), PointReference.ORDER);
        assertEquals(transactionSummary.getReferenceKey(), referenceKey);

        List<PointTransactionEntity> transactions = pointTransactionRepository.findAll().stream().filter(d -> Objects.equals(d.getSummaryId(), transactionSummary.getId())).toList();
        assertEquals(transactions.size(), 2);

        {
            PointTransactionEntity transaction = transactions.get(0);
            assertEquals(transaction.getStatus(), PointStatus.EARNED);
            assertEquals(transaction.getBeforeBalance().compareTo(BigDecimal.ZERO), 0);
            assertEquals(transaction.getAmount().compareTo(BigDecimal.TEN), 0);
            assertEquals(transaction.getBalance().compareTo(BigDecimal.TEN), 0);
        }

        {
            PointTransactionEntity transaction = transactions.get(1);
            assertEquals(transaction.getStatus(), PointStatus.EARNED);
            assertEquals(transaction.getBeforeBalance().compareTo(BigDecimal.valueOf(8)), 0);
            assertEquals(transaction.getAmount().compareTo(BigDecimal.ONE), 0);
            assertEquals(transaction.getBalance().compareTo(BigDecimal.valueOf(9)), 0);
        }
    }

    @Test
    void 포인트_사용취소_실패통합테스트() throws Exception {
        earn();
        earn();

        {
            PointUseRequest request = new PointUseRequest(
                    userId,
                    BigDecimal.valueOf(12),
                    PointReference.ORDER,
                    referenceKey
            );

            mockMvc.perform(post("/api/points/:use")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
        }

        PointUseCancelRequest request = new PointUseCancelRequest(
                userId,
                BigDecimal.valueOf(20),
                PointReference.ORDER,
                referenceKey
        );

        MvcResult result = mockMvc.perform(post("/api/points/:useCancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ApiResponse<PointUseCancelResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(apiResponse.status(), ApiStatus.ERROR);
        assertTrue(apiResponse.message().isPresent());
        assertTrue(apiResponse.data().isEmpty());

        List<PointEntity> points = pointRepository.findAll();
        assertEquals(points.size(), 2);
        List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll();
        assertEquals(transactionSummaries.size(), 3);
        List<PointTransactionEntity> transactions = pointTransactionRepository.findAll();
        assertEquals(transactions.size(), 4);
    }
}
