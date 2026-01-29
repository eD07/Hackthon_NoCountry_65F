package com.hackathon.churninsight.controller;

import com.hackathon.churninsight.dto.request.CustomerFeaturesDTO;
import com.hackathon.churninsight.dto.request.PredictRequestDTO;
import com.hackathon.churninsight.dto.response.PredictResponseDTO;
import com.hackathon.churninsight.dto.response.PredictionResultDTO;
import com.hackathon.churninsight.dto.response.SuccessResponseDTO;
import com.hackathon.churninsight.service.PredictService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link PredictController}.
 *
 * <p>
 * Esta clase valida el comportamiento del endpoint de predicción de churn,
 * asegurando que el controlador:
 * </p>
 *
 * <ul>
 *   <li>Invoque correctamente al {@link PredictService}</li>
 *   <li>Interprete adecuadamente el resultado del modelo</li>
 *   <li>Genere una respuesta legible para el usuario final</li>
 * </ul>
 *
 * <p>
 * Se simulan distintos escenarios de predicción utilizando Mockito,
 * sin necesidad de levantar el contexto completo de Spring.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class PredictControllerTest {

    /**
     * Servicio de predicción mockeado.
     */
    @Mock
    private PredictService predictService;

    /**
     * Request HTTP simulado para obtener el path de la solicitud.
     */
    @Mock
    private HttpServletRequest httpServletRequest;

    /**
     * Controlador bajo prueba, con dependencias inyectadas.
     */
    @InjectMocks
    private PredictController predictController;

    /**
     * Request base reutilizado en los tests.
     */
    private PredictRequestDTO mockRequest;

    /**
     * Configura el escenario común previo a cada test.
     *
     * <p>
     * - Se construye un {@link PredictRequestDTO} con datos de ejemplo
     * - Se simula el path del request HTTP
     * </p>
     */
    @BeforeEach
    void setUp() {
        // 1. Instanciamos features del cliente
        CustomerFeaturesDTO features = new CustomerFeaturesDTO(
                null, 100.0, 5, 15.0, 2, 2.0, null
        );

        // 2. Creamos el request de predicción
        mockRequest = new PredictRequestDTO("user-123", features);

        // 3. Simulamos el path del request HTTP
        lenient().when(httpServletRequest.getRequestURI())
                .thenReturn("/api/predict");
    }

    /**
     * Verifica que cuando el modelo predice churn,
     * el controlador retorne una previsualización positiva de cancelación.
     *
     * <p>
     * Escenario:
     * <ul>
     *   <li>El servicio retorna la etiqueta {@code will_churn}</li>
     * </ul>
     *
     * Resultado esperado:
     * <ul>
     *   <li>Status HTTP 200</li>
     *   <li>Previsión: "Va a cancelar"</li>
     *   <li>Customer ID correcto</li>
     * </ul>
     */
    @Test
    @SuppressWarnings("unchecked")
    void predict_WhenCustomerWillChurn_ShouldReturnPositivePrevision() {
        // Arrange
        PredictionResultDTO predictionResult =
                new PredictionResultDTO("will_churn", 0.82);

        PredictResponseDTO mockResponse =
                new PredictResponseDTO("user-123", predictionResult);

        when(predictService.predict(any(PredictRequestDTO.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<SuccessResponseDTO<Object>> response =
                predictController.predict(mockRequest, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> data =
                (Map<String, Object>) response.getBody().data();

        assertEquals("Va a cancelar", data.get("prevision"));
        assertEquals("user-123", data.get("customer_id"));

        verify(predictService, times(1)).predict(any());
    }

    /**
     * Verifica que cuando el modelo predice continuidad,
     * el controlador retorne una previsualización negativa de cancelación.
     *
     * <p>
     * Escenario:
     * <ul>
     *   <li>El servicio retorna la etiqueta {@code will_continue}</li>
     * </ul>
     *
     * Resultado esperado:
     * <ul>
     *   <li>Status HTTP 200</li>
     *   <li>Previsión: "Va a continuar"</li>
     * </ul>
     */
    @Test
    @SuppressWarnings("unchecked")
    void predict_WhenCustomerWillContinue_ShouldReturnNegativePrevision() {
        // Arrange
        PredictionResultDTO predictionResult =
                new PredictionResultDTO("will_continue", 0.15);

        PredictResponseDTO stayResponse =
                new PredictResponseDTO("user-123", predictionResult);

        when(predictService.predict(any(PredictRequestDTO.class)))
                .thenReturn(stayResponse);

        // Act
        ResponseEntity<SuccessResponseDTO<Object>> response =
                predictController.predict(mockRequest, httpServletRequest);

        // Assert
        Map<String, Object> data =
                (Map<String, Object>) response.getBody().data();

        assertEquals("Va a continuar", data.get("prevision"));
    }
}

