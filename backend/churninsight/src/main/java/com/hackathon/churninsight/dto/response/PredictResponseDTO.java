package com.hackathon.churninsight.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Schema(
        name = "PredictResponse",
        description = "Respuesta técnica generada por el servicio de predicción"
)
public record PredictResponseDTO(
        @NotBlank
        @Schema(
                description = "Identificador del cliente",
                example = "user-123"
        )
        @JsonProperty("customer_id")
        String customerId,

        @NotNull
        @Valid
        @Schema(
                description = "Resultado de la predicción"
        )
        @JsonProperty("prediction")
        PredictionResultDTO prediction
) {}
