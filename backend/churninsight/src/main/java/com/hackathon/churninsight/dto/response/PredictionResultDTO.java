package com.hackathon.churninsight.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(
        name = "PredictionResult",
        description = "Resultado de la predicción de churn"
)
public record PredictionResultDTO(
        @NotBlank
        @Pattern(
                regexp = "^(will_churn|will_continue)$",
                message = "Label inválido"
        )
        @Schema(
                description = "Etiqueta de predicción generada por el modelo",
                example = "will_churn",
                allowableValues = {"will_churn", "will_continue"}
        )
        @JsonProperty("label")
        String label,

        @DecimalMin(value = "0.0", inclusive = true)
        @DecimalMax(value = "1.0", inclusive = true)
        @Schema(
                description = "Probabilidad asociada a la predicción",
                example = "0.87",
                minimum = "0",
                maximum = "1"
        )
        @JsonProperty("probability")
        double probability
) {}
