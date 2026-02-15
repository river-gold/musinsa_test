package musinsa.test.domain.point.api.point.earn;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import musinsa.test.api.common.ApiResponse;
import musinsa.test.api.common.ApiStatus;
import musinsa.test.api.point.earn.PointEarnCancelRequest;
import musinsa.test.api.point.earn.PointEarnCancelResponse;
import musinsa.test.api.point.earn.PointEarnRequest;
import musinsa.test.api.point.earn.PointEarnResponse;
import musinsa.test.api.point.earn.PointManualEarnRequest;
import musinsa.test.domain.point.code.PointAction;
import musinsa.test.domain.point.code.PointEarnType;
import musinsa.test.domain.point.code.PointStatus;
import musinsa.test.entity.point.PointEntity;
import musinsa.test.entity.point.PointTransactionSummaryEntity;
import musinsa.test.entity.point.PointTransactionEntity;
import musinsa.test.entity.point.PointUserConfigEntity;
import musinsa.test.entity.point.repo.PointRepository;
import musinsa.test.entity.point.repo.PointTransactionRepository;
import musinsa.test.entity.point.repo.PointTransactionSummaryRepository;
import musinsa.test.entity.point.repo.PointUserConfigRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PointEarnControllerTest {
    private final Long userId = 123L;
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
    @Autowired
    private PointUserConfigRepository pointUserConfigRepository;

    @Test
    void 포인트_적립_통합테스트() throws Exception {
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
        assertEquals(apiResponse.status(), ApiStatus.SUCCESS);
        assertTrue(apiResponse.message().isEmpty());

        PointEarnResponse response = apiResponse.data().orElseThrow();
        assertEquals(response.userId(), userId);
        assertEquals(response.amount(), BigDecimal.TEN);
        assertEquals(response.expireDate(), LocalDate.now().plusDays(365));
        assertEquals(response.status(), PointStatus.EARNED);
        assertNotNull(response.pointKey());

        List<PointEntity> points = pointRepository.findAll();
        assertEquals(points.size(), 1);

        PointEntity point = points.stream().filter(p -> p.getPointKey().equals(response.pointKey())).findFirst().orElseThrow();
        assertEquals(point.getUserId(), userId);
        assertEquals(point.getEarnedAmount().compareTo(BigDecimal.TEN), 0);
        assertEquals(point.getBalance().compareTo(BigDecimal.TEN), 0);
        assertEquals(point.getExpireDate(), LocalDate.now().plusDays(365));
        assertEquals(point.getStatus(), PointStatus.EARNED);
        assertEquals(point.getEarnType(), PointEarnType.SYSTEM);
        assertNull(point.getIssuerId());

        List<PointTransactionEntity> transactions = pointTransactionRepository.findAll();
        assertEquals(transactions.size(), 1);

        PointTransactionEntity transaction = transactions.stream().filter(d -> d.getPointId().equals(point.getId())).findFirst().orElseThrow();
        assertEquals(transaction.getStatus(), PointStatus.EARNED);
        assertEquals(transaction.getBeforeBalance().compareTo(BigDecimal.ZERO), 0);
        assertEquals(transaction.getAmount().compareTo(BigDecimal.TEN), 0);
        assertEquals(transaction.getBalance().compareTo(BigDecimal.TEN), 0);

        List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll();
        assertEquals(transactionSummaries.size(), 1);

        PointTransactionSummaryEntity transactionSummary = transactionSummaries.stream().filter(d -> d.getId().equals(transaction.getSummaryId())).findFirst().orElseThrow();
        assertEquals(transactionSummary.getAction(), PointAction.EARN);
        assertEquals(transactionSummary.getUserId(), userId);
        assertEquals(transactionSummary.getBeforeSumBalance().compareTo(BigDecimal.ZERO), 0);
        assertEquals(transactionSummary.getAmount().compareTo(BigDecimal.TEN), 0);
        assertEquals(transactionSummary.getSumBalance().compareTo(BigDecimal.TEN), 0);
        assertNull(transactionSummary.getReference());
        assertNull(transactionSummary.getReferenceKey());
    }

    @Test
    void 포인트_적립_실패통합테스트() throws Exception {
        // 1회 적립 한도 테스트
        {
            PointEarnRequest request = new PointEarnRequest(
                    userId,
                    BigDecimal.valueOf(100001),
                    Optional.empty()
            );

            MvcResult result = mockMvc.perform(post("/api/points/:earn")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andDo(print())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            ApiResponse<PointEarnResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
            });
            assertEquals(apiResponse.status(), ApiStatus.ERROR);
            assertTrue(apiResponse.message().isPresent());
            assertTrue(apiResponse.data().isEmpty());

            List<PointEntity> points = pointRepository.findAll();
            assertTrue(points.isEmpty());
            List<PointTransactionEntity> transactions = pointTransactionRepository.findAll();
            assertTrue(transactions.isEmpty());
            List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll();
            assertTrue(transactionSummaries.isEmpty());
        }
        // 개인별 보유 한도 테스트
        {
            // 개인별 보유가능한 무료포인트
            pointUserConfigRepository.save(PointUserConfigEntity.builder().userId(userId).maxBalance(BigDecimal.valueOf(10000)).build());

            PointEarnRequest request = new PointEarnRequest(
                    userId,
                    BigDecimal.valueOf(10001),
                    Optional.empty()
            );

            MvcResult result = mockMvc.perform(post("/api/points/:earn")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andDo(print())
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            ApiResponse<PointEarnResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
            });
            assertEquals(apiResponse.status(), ApiStatus.ERROR);
            assertTrue(apiResponse.message().isPresent());
            assertTrue(apiResponse.data().isEmpty());

            List<PointEntity> points = pointRepository.findAll();
            assertTrue(points.isEmpty());
            List<PointTransactionEntity> transactions = pointTransactionRepository.findAll();
            assertTrue(transactions.isEmpty());
            List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll();
            assertTrue(transactionSummaries.isEmpty());
        }
    }

    @Test
    void 관리자_포인트_적립_통합테스트() throws Exception {
        // given
        PointManualEarnRequest request = new PointManualEarnRequest(
                userId,
                BigDecimal.TEN,
                Optional.empty(),
                "admin"
        );

        // when & then
        MvcResult result = mockMvc.perform(post("/api/points/:manualEarn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ApiResponse<PointEarnResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(apiResponse.status(), ApiStatus.SUCCESS);
        assertEquals(apiResponse.message(), Optional.empty());

        PointEarnResponse response = apiResponse.data().orElseThrow();
        assertEquals(response.userId(), userId);
        assertEquals(response.amount(), BigDecimal.TEN);
        assertEquals(response.expireDate(), LocalDate.now().plusDays(365));
        assertEquals(response.status(), PointStatus.EARNED);
        assertNotNull(response.pointKey());

        List<PointEntity> points = pointRepository.findAll();
        assertEquals(points.size(), 1);

        PointEntity point = points.stream().filter(p -> p.getPointKey().equals(response.pointKey())).findFirst().orElseThrow();
        assertEquals(point.getUserId(), userId);
        assertEquals(point.getEarnedAmount().compareTo(BigDecimal.TEN), 0);
        assertEquals(point.getBalance().compareTo(BigDecimal.TEN), 0);
        assertEquals(point.getExpireDate(), LocalDate.now().plusDays(365));
        assertEquals(point.getStatus(), PointStatus.EARNED);
        assertEquals(point.getEarnType(), PointEarnType.ADMIN);
        assertEquals(point.getIssuerId(), "admin");

        List<PointTransactionEntity> transactions = pointTransactionRepository.findAll();
        assertEquals(transactions.size(), 1);

        PointTransactionEntity transaction = transactions.stream().filter(d -> d.getPointId().equals(point.getId())).findFirst().orElseThrow();
        assertEquals(transaction.getStatus(), PointStatus.EARNED);
        assertEquals(transaction.getBeforeBalance().compareTo(BigDecimal.ZERO), 0);
        assertEquals(transaction.getAmount().compareTo(BigDecimal.TEN), 0);
        assertEquals(transaction.getBalance().compareTo(BigDecimal.TEN), 0);

        List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll();
        assertEquals(transactionSummaries.size(), 1);

        PointTransactionSummaryEntity transactionSummary = transactionSummaries.stream().filter(d -> d.getId().equals(transaction.getSummaryId())).findFirst().orElseThrow();
        assertEquals(transactionSummary.getAction(), PointAction.EARN);
        assertEquals(transactionSummary.getUserId(), userId);
        assertEquals(transactionSummary.getBeforeSumBalance().compareTo(BigDecimal.ZERO), 0);
        assertEquals(transactionSummary.getAmount().compareTo(BigDecimal.TEN), 0);
        assertEquals(transactionSummary.getSumBalance().compareTo(BigDecimal.TEN), 0);
        assertNull(transactionSummary.getReference());
        assertNull(transactionSummary.getReferenceKey());
    }

    @Test
    void 포인트_적립취소_통합테스트() throws Exception {
        // 성공
        String pointKey;
        {
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
            pointKey = response.pointKey();
        }

        PointEarnCancelRequest request = new PointEarnCancelRequest(userId);

        MvcResult result = mockMvc.perform(patch("/api/points/%s/:cancel".formatted(pointKey))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ApiResponse<PointEarnCancelResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(apiResponse.status(), ApiStatus.SUCCESS);
        assertTrue(apiResponse.message().isEmpty());

        PointEarnCancelResponse response = apiResponse.data().orElseThrow();
        assertEquals(response.userId(), userId);
        assertEquals(response.amount(), BigDecimal.TEN);
        assertEquals(response.expireDate(), LocalDate.now().plusDays(365));
        assertEquals(response.status(), PointStatus.CANCEL);
        assertNotNull(response.pointKey());

        List<PointEntity> points = pointRepository.findAll();
        assertEquals(points.size(), 1);

        PointEntity point = points.stream().filter(p -> p.getPointKey().equals(response.pointKey())).findFirst().orElseThrow();
        assertEquals(point.getUserId(), userId);
        assertEquals(point.getEarnedAmount().compareTo(BigDecimal.TEN), 0);
        assertEquals(point.getBalance().compareTo(BigDecimal.ZERO), 0);
        assertEquals(point.getExpireDate(), LocalDate.now().plusDays(365));
        assertEquals(point.getStatus(), PointStatus.CANCEL);
        assertEquals(point.getEarnType(), PointEarnType.SYSTEM);
        assertNull(point.getIssuerId());

        List<PointTransactionEntity> transactions = pointTransactionRepository.findAll();
        assertEquals(transactions.size(), 2);

        PointTransactionEntity transaction = transactions.stream().filter(d -> d.getPointId().equals(point.getId()) && d.getStatus().equals(PointStatus.CANCEL)).findFirst().orElseThrow();
        assertEquals(transaction.getStatus(), PointStatus.CANCEL);
        assertEquals(transaction.getBeforeBalance().compareTo(BigDecimal.TEN), 0);
        assertEquals(transaction.getAmount().compareTo(BigDecimal.TEN.negate()), 0);
        assertEquals(transaction.getBalance().compareTo(BigDecimal.ZERO), 0);

        List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll();
        assertEquals(transactionSummaries.size(), 2);

        PointTransactionSummaryEntity transactionSummary = transactionSummaries.stream().filter(d -> d.getId().equals(transaction.getSummaryId())).findFirst().orElseThrow();
        assertEquals(transactionSummary.getAction(), PointAction.EARN_CANCEL);
        assertEquals(transactionSummary.getUserId(), userId);
        assertEquals(transactionSummary.getBeforeSumBalance().compareTo(BigDecimal.TEN), 0);
        assertEquals(transactionSummary.getAmount().compareTo(BigDecimal.TEN.negate()), 0);
        assertEquals(transactionSummary.getSumBalance().compareTo(BigDecimal.ZERO), 0);
        assertNull(transactionSummary.getReference());
        assertNull(transactionSummary.getReferenceKey());
    }

    @Test
    void 포인트_적립취소_실패통합테스트() throws Exception {
        // 성공
        String pointKey;
        {
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
            pointKey = response.pointKey();

            List<PointEntity> points = pointRepository.findAll();

            //  포인트 사용됨
            PointEntity point = points.stream().filter(p -> p.getPointKey().equals(response.pointKey())).findFirst().orElseThrow();
            point.setBalance(BigDecimal.ONE);
        }

        PointEarnCancelRequest request = new PointEarnCancelRequest(userId);

        MvcResult result = mockMvc.perform(patch("/api/points/%s/:cancel".formatted(pointKey))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andDo(print())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        ApiResponse<PointEarnResponse> apiResponse = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(apiResponse.status(), ApiStatus.ERROR);
        assertTrue(apiResponse.message().isPresent());
        assertTrue(apiResponse.data().isEmpty());

        List<PointEntity> points = pointRepository.findAll();
        assertEquals(points.size(), 1);
        List<PointTransactionEntity> transactions = pointTransactionRepository.findAll();
        assertEquals(transactions.size(), 1);
        List<PointTransactionSummaryEntity> transactionSummaries = pointTransactionSummaryRepository.findAll();
        assertEquals(transactionSummaries.size(), 1);
    }
}
