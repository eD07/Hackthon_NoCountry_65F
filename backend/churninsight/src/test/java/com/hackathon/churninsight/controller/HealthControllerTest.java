package com.hackathon.churninsight.controller;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link HealthController}.
 *
 * <p>
 * Esta clase valida el comportamiento de los endpoints de salud del backend,
 * simulando la comunicación con el microservicio de Machine Learning mediante
 * {@link MockWebServer}.
 * </p>
 *
 * <p>
 * Se prueban los siguientes escenarios:
 * <ul>
 *   <li>Servicio ML disponible (UP)</li>
 *   <li>Servicio ML no disponible (DOWN)</li>
 *   <li>Endpoint básico de salud, independiente del ML</li>
 * </ul>
 * </p>
 */
class HealthControllerTest {

    private MockWebServer mockWebServer;
    private HealthController healthController;

    /**
     * Configura el entorno de prueba antes de cada test.
     *
     * <p>
     * - Inicia un {@link MockWebServer} para simular el microservicio de ML.
     * - Crea un {@link WebClient} apuntando al servidor mock.
     * - Instancia manualmente el {@link HealthController}.
     * - Inyecta el valor de {@code mlServiceBaseUrl} usando reflexión,
     *   ya que normalmente Spring lo hace vía {@code @Value}.
     * </p>
     */
    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        healthController = new HealthController(webClient);

        ReflectionTestUtils.setField(
                healthController,
                "mlServiceBaseUrl",
                "http://localhost:8080"
        );
    }

    /**
     * Detiene el {@link MockWebServer} luego de cada test
     * para liberar recursos.
     */
    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    /**
     * Verifica que el endpoint /health responda OK
     * cuando el microservicio de ML está disponible.
     *
     * <p>
     * Escenario:
     * <ul>
     *   <li>El servicio ML responde HTTP 200</li>
     * </ul>
     *
     * Resultado esperado:
     * <ul>
     *   <li>Status HTTP 200 (OK)</li>
     *   <li>status = UP</li>
     *   <li>ml = UP</li>
     * </ul>
     */
    @Test
    void health_WhenMlServiceIsUp_ShouldReturnOk() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        ResponseEntity<Map<String, Object>> response = healthController.health();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("UP", response.getBody().get("ml"));
    }

    /**
     * Verifica que el endpoint /health responda SERVICE_UNAVAILABLE
     * cuando el microservicio de ML no está disponible.
     *
     * <p>
     * Escenario:
     * <ul>
     *   <li>El servicio ML responde HTTP 500</li>
     * </ul>
     *
     * Resultado esperado:
     * <ul>
     *   <li>Status HTTP 503 (SERVICE_UNAVAILABLE)</li>
     *   <li>status = DEGRADED</li>
     *   <li>ml = DOWN</li>
     * </ul>
     */
    @Test
    void health_WhenMlServiceIsDown_ShouldReturnServiceUnavailable() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        ResponseEntity<Map<String, Object>> response = healthController.health();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("DEGRADED", response.getBody().get("status"));
        assertEquals("DOWN", response.getBody().get("ml"));
    }

    /**
     * Verifica que el endpoint /basic siempre responda OK,
     * independientemente del estado del microservicio de ML.
     */
    @Test
    void basic_ShouldAlwaysReturnOk() {
        ResponseEntity<Map<String, Object>> response = healthController.basic();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("ChurnInsight Backend", response.getBody().get("service"));
    }
}

