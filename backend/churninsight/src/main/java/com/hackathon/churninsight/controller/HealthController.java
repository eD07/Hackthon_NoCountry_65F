package com.hackathon.churninsight.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para health checks y verificación del estado del servicio.
 * Proporciona endpoints para monitoreo y observabilidad.
 */
@RestController
@RequestMapping("/api/health")
@Slf4j
@Tag(name = "Health", description = "Endpoints para monitoreo del estado del backend y servicios dependientes")
public class HealthController {

    private final WebClient webClient;

    @Value("${ml.service.base-url}")
    private String mlServiceBaseUrl;

    public HealthController(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * ✅ Health PRO: backend valida conectividad con ML (server-to-server).
     * Este es el endpoint que debe consumir el FRONTEND.
     */
    @Operation(
            summary = "Health PRO",
            description = "Valida el estado del backend y la conectividad con el servicio de Machine Learning.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Backend y ML operativos"),
                    @ApiResponse(responseCode = "503", description = "ML caído o degradado")
            }
    )
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Health PRO solicitado (/api/health)");

        Map<String, Object> health = baseInfo();

        boolean mlUp = checkMlServiceHealth();

        Map<String, Object> dependencies = new HashMap<>();
        dependencies.put("ml-service", Map.of(
                "url", mlServiceBaseUrl,
                "status", mlUp ? "UP" : "DOWN"
        ));

        health.put("dependencies", dependencies);
        health.put("backend", "UP");
        health.put("ml", mlUp ? "UP" : "DOWN");
        health.put("status", mlUp ? "UP" : "DEGRADED");

        HttpStatus status = mlUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(health);
    }

    /**
     * Health básico (sin validar dependencias).
     * Útil para checks rápidos o uptime.
     */
    @Operation(
            summary = "Health básico",
            description = "Retorna el estado del backend sin validar servicios externos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Backend operativo")
            }
    )
    @GetMapping("/basic")
    public ResponseEntity<Map<String, Object>> basic() {
        log.debug("Health básico solicitado (/api/health/basic)");

        Map<String, Object> health = baseInfo();
        health.put("backend", "UP");
        health.put("status", "UP");

        return ResponseEntity.ok(health);
    }

    private Map<String, Object> baseInfo() {
        Map<String, Object> health = new HashMap<>();
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "ChurnInsight Backend");
        health.put("version", "1.0.0");
        return health;
    }

    /**
     * Verifica si el servicio ML está disponible.
     * OJO: WebClient ya tiene baseUrl configurada => usamos uri("/health").
     */
    private boolean checkMlServiceHealth() {
        try {
            webClient.get()
                    .uri("/health")
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(2))
                    .block();
            return true;
        } catch (Exception e) {
            log.warn("Servicio ML no disponible: {}", e.getMessage());
            return false;
        }
    }
}
