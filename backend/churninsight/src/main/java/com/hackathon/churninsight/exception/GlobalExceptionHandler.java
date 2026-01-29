package com.hackathon.churninsight.exception;

import com.hackathon.churninsight.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura y procesa todas las excepciones lanzadas por los controllers,
 * retornando respuestas de error estructuradas y consistentes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                Map<String, String> details = new HashMap<>();

                ex.getBindingResult().getFieldErrors().forEach(error -> details.put(
                                error.getField(),
                                error.getDefaultMessage()));

                ex.getBindingResult().getGlobalErrors().forEach(error -> details.put(
                                error.getObjectName(),
                                error.getDefaultMessage()));

                return buildErrorResponse(
                                HttpStatus.BAD_REQUEST,
                                "Error de Validación",
                                details,
                                request.getRequestURI());
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponseDTO> handleMalformedJson(
                HttpMessageNotReadableException ex,
                HttpServletRequest request) {

            Throwable cause = ex.getCause();
            while (cause != null) {
                if (cause instanceof IllegalArgumentException iae) {
                    Map<String, String> details = new HashMap<>();
                    details.put("error", iae.getMessage());

                    return buildErrorResponse(
                            HttpStatus.BAD_REQUEST,
                            "Valor inválido",
                            details,
                            request.getRequestURI()
                    );
                }
                cause = cause.getCause();
            }

            // fallback: JSON realmente mal formado
            Map<String, String> details = new HashMap<>();
            details.put("error", "El JSON enviado está mal formado o incompleto");
            details.put(
                    "detalle",
                    "Verifica la sintaxis, tipos de datos y que no existan valores vacíos");

            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "JSON Mal Formado",
                    details,
                    request.getRequestURI());
        }

    @ExceptionHandler(ExternalServiceException.class)
        public ResponseEntity<ErrorResponseDTO> handleExternalServiceError(
                        ExternalServiceException ex,
                        HttpServletRequest request) {
                log.error("Servicio externo no disponible: {}", ex.getMessage(), ex);
                Map<String, String> details = new HashMap<>();
                details.put("error", "Servicio de predicción no disponible");
                details.put("message", ex.getMessage());
                details.put("recomendación", "Intente nuevamente en unos momentos");

                return buildErrorResponse(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "Servicio Externo No Disponible",
                                details,
                                request.getRequestURI());
        }

        @ExceptionHandler(TimeoutException.class)
        public ResponseEntity<ErrorResponseDTO> handleTimeout(
                        TimeoutException ex,
                        HttpServletRequest request) {
                log.warn("Timeout en servicio externo: {}", ex.getMessage());
                Map<String, String> details = new HashMap<>();
                details.put("error", "La solicitud tardó demasiado tiempo");
                details.put("sugerencia", "El servicio ML puede estar sobrecargado");

                return buildErrorResponse(
                                HttpStatus.REQUEST_TIMEOUT,
                                "Timeout en Servicio Externo",
                                details,
                                request.getRequestURI());
        }

        @ExceptionHandler(WebClientRequestException.class)
        public ResponseEntity<ErrorResponseDTO> handleWebClientRequestError(
                        WebClientRequestException ex,
                        HttpServletRequest request) {
                Map<String, String> details = new HashMap<>();
                details.put("error", "No se pudo conectar con el servicio de ML");
                details.put("causa", "El servicio puede estar caído o no disponible");
                details.put("mensaje", ex.getMessage());

                return buildErrorResponse(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "Error de Conexión",
                                details,
                                request.getRequestURI());
        }

        @ExceptionHandler(WebClientResponseException.class)
        public ResponseEntity<ErrorResponseDTO> handleWebClientResponseError(
                        WebClientResponseException ex,
                        HttpServletRequest request) {
                Map<String, String> details = new HashMap<>();
                details.put("error", "El servicio ML retornó un error");
                details.put("status", String.valueOf(ex.getStatusCode().value()));
                details.put("response", ex.getResponseBodyAsString());

                return buildErrorResponse(
                                HttpStatus.valueOf(ex.getStatusCode().value()),
                                "Error en Servicio ML",
                                details,
                                request.getRequestURI());
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
                IllegalArgumentException ex,
                HttpServletRequest request) {

            Map<String, String> details = new HashMap<>();
            details.put("error", ex.getMessage());

            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    "Valor inválido",
                    details,
                    request.getRequestURI()
            );
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDTO> handleGeneralException(
                        Exception ex,
                        HttpServletRequest request) {
                log.error("Error inesperado: {}", ex.getMessage(), ex);
                Map<String, String> details = new HashMap<>();
                details.put("exception", ex.getClass().getSimpleName());
                details.put("message", "Ocurrió un error inesperado en el servidor");

                return buildErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Error Interno del Servidor",
                                details,
                                request.getRequestURI());
        }

        private ResponseEntity<ErrorResponseDTO> buildErrorResponse(
                        HttpStatus status,
                        String error,
                        Map<String, ?> details,
                        String path) {
                ErrorResponseDTO response = new ErrorResponseDTO(
                                LocalDateTime.now(),
                                status.value(),
                                error,
                                details,
                                path);

                return ResponseEntity.status(status).body(response);
        }
}
