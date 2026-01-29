package com.hackathon.churninsight.exception;

import com.hackathon.churninsight.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        // Configuramos un URI por defecto para las pruebas
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    @DisplayName("Debería manejar ExternalServiceException y retornar SERVICE_UNAVAILABLE")
    void handleExternalServiceErrorTest() {
        ExternalServiceException ex = new ExternalServiceException("Error en servicio ML");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleExternalServiceError(ex, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Servicio de predicción no disponible", response.getBody().details().get("error"));
    }

    @Test
    @DisplayName("Debería manejar IllegalArgumentException y retornar BAD_REQUEST")
    void handleIllegalArgumentExceptionTest() {
        IllegalArgumentException ex = new IllegalArgumentException("Dato inválido");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleIllegalArgumentException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Valor inválido", response.getBody().error());
        assertEquals("Dato inválido", response.getBody().details().get("error"));
    }

    @Test
    @DisplayName("Debería manejar Exception genérica y retornar INTERNAL_SERVER_ERROR")
    void handleGeneralExceptionTest() {
        Exception ex = new Exception("Error inesperado");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error Interno del Servidor", response.getBody().error());
    }
}
