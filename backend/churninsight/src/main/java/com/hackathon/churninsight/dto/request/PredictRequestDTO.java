package com.hackathon.churninsight.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Schema(
        name = "PredictRequest",
        description = "Solicitud de predicción de churn para un cliente"
)
public record PredictRequestDTO(
        @NotBlank(message = "El customer_id es obligatorio")
        @Schema(
                description = "Identificador único del cliente",
                example = "user-123"
        )
        @JsonProperty("customer_id")
        String customerId,

        @NotNull(message = "El objeto features es obligatorio")
        @Valid
        @Schema(
                description = "Características del cliente necesarias para la predicción"
        )
        @JsonProperty("features")
        CustomerFeaturesDTO features
) {}
