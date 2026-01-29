package com.hackathon.churninsight.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(
        name = "SuccessResponse",
        description = "Respuesta estándar exitosa de la API"
)
public record SuccessResponseDTO<T>(
        @Schema(
                description = "Fecha y hora de la respuesta",
                example = "2026-01-10T14:30:00"
        )
        LocalDateTime timestamp,

        @Schema(
                description = "Código HTTP de la respuesta",
                example = "200"
        )
        int status,

        @Schema(
                description = "Mensaje descriptivo de la operación",
                example = "Predicción generada correctamente"
        )
        String message,

        @Schema(
                description = "Datos retornados por la operación"
        )
        T data,

        @Schema(
                description = "Path del endpoint invocado",
                example = "/api/predict"
        )
        String path
) {}

