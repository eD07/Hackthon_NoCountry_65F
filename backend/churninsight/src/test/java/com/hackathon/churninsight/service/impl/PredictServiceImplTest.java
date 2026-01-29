package com.hackathon.churninsight.service.impl;

import com.hackathon.churninsight.domain.enums.PaymentMethod;
import com.hackathon.churninsight.domain.enums.SubscriptionType;
import com.hackathon.churninsight.dto.request.CustomerFeaturesDTO;
import com.hackathon.churninsight.dto.request.PredictRequestDTO;
import com.hackathon.churninsight.dto.response.PredictResponseDTO;
import com.hackathon.churninsight.exception.ExternalServiceException;
import com.hackathon.churninsight.repository.PredictionHistoryRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class PredictServiceImplTest {

    private MockWebServer mockWebServer;
    private PredictServiceImpl predictService;

    @Mock
    private PredictionHistoryRepository predictionHistoryRepository;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        predictService = new PredictServiceImpl(
                webClient,
                predictionHistoryRepository
        );

        ReflectionTestUtils.setField(predictService, "timeout", 3000);
        ReflectionTestUtils.setField(predictService, "maxRetryAttempts", 0);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnPredictionAndSaveHistoryWhenMlServiceReturns200() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                    {
                      "prediction": {
                        "label": "NO_CHURN",
                        "probability": 0.12
                      }
                    }
                """));

        CustomerFeaturesDTO features = new CustomerFeaturesDTO(
                SubscriptionType.BASIC,
                120.5,
                3,
                9.99,
                2,
                2.5,
                PaymentMethod.CREDIT_CARD
        );

        PredictRequestDTO request = new PredictRequestDTO("customer-123", features);

        // Act
        PredictResponseDTO response = predictService.predict(request);

        // Assert
        assertNotNull(response);
        assertEquals("NO_CHURN", response.prediction().label());
        assertEquals(0.12, response.prediction().probability());

        // 🔹 Verificar que se guarda el historial
        verify(predictionHistoryRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenMlServiceReturns4xxAndNotSaveHistory() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Invalid request"));

        CustomerFeaturesDTO features = new CustomerFeaturesDTO(
                SubscriptionType.PREMIUM,
                50.0,
                10,
                17.99,
                3,
                1.5,
                PaymentMethod.DEBIT_CARD
        );

        PredictRequestDTO request = new PredictRequestDTO("customer-456", features);

        // Act & Assert
        assertThrows(ExternalServiceException.class,
                () -> predictService.predict(request));

        // 🔹 No se debe guardar historial
        verify(predictionHistoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenMlServiceReturns5xxAndNotSaveHistory() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500));

        CustomerFeaturesDTO features = new CustomerFeaturesDTO(
                SubscriptionType.STANDARD,
                20.0,
                30,
                13.99,
                1,
                0.8,
                PaymentMethod.PAYPAL
        );

        PredictRequestDTO request = new PredictRequestDTO("customer-789", features);

        // Act & Assert
        assertThrows(ExternalServiceException.class,
                () -> predictService.predict(request));

        verify(predictionHistoryRepository, never()).save(any());
    }
}



