package com.hackathon.churninsight.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(
        name = "ErrorResponse",
        description = "Respuesta estándar de error de la API"
)
public record ErrorResponseDTO(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(
                description = "Fecha y hora del error",
                example = "2026-01-10T14:32:10"
        )
        LocalDateTime timestamp,

        @Schema(
                description = "Código HTTP del error",
                example = "400"
        )
        int status,

        @Schema(
                description = "Tipo de error",
                example = "Bad Request"
        )
        String error,

        @Schema(
                description = "Detalle del error o errores asociados"
        )
        Map<String, ?> details,

        @Schema(
                description = "Path del endpoint que generó el error",
                example = "/api/predict"
        )
        String path
) {}
