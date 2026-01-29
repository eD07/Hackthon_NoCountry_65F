package com.hackathon.churninsight.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon.churninsight.domain.enums.PaymentMethod;
import com.hackathon.churninsight.domain.enums.SubscriptionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(
        name = "CustomerFeatures",
        description = "Características del cliente utilizadas para la predicción de churn"
)
public record CustomerFeaturesDTO(
        @NotNull(message = "El tipo de suscripción es obligatorio")
        @Schema(
                description = "Tipo de suscripción",
                example = "Premium",
                allowableValues = {"Basic", "Standard", "Premium"}
        )
        @JsonProperty("subscription_type")
        SubscriptionType subscriptionType,

        @NotNull (message = "Las horas vistas son obligatorias")
        @DecimalMin(value = "0.0", message = "Las horas vistas deben ser mayores a 0")
        @Schema(
                description = "Total de horas vistas por el cliente",
                example = "120.5"
        )
        @JsonProperty("watch_hours")
        Double watchHours,

        @NotNull(message = "Los días desde el último login son obligatorios")
        @Min(value = 0, message = "Los días desde el último login no pueden ser negativos")
        @Schema(
                description = "Cantidad de días desde el último inicio de sesión",
                example = "3"
        )
        @JsonProperty("last_login_days")
        Integer lastLoginDays,

        @NotNull(message = "La tarifa mensual es obligatoria")
        @Schema(
                description = "Tarifa mensual del plan contratado",
                example = "17.99"
        )
        @JsonProperty("monthly_fee")
        Double monthlyFee,

        @NotNull(message = "El número de perfiles es obligatorio")
        @Min(value = 1, message = "el valor debe estar entre 1 y 5")
        @Max(value = 5, message = "el valor debe estar entre 1 y 5")
        @Schema(
                description = "Número de perfiles asociados a la cuenta",
                example = "4",
                minimum = "1",
                maximum = "5"
        )
        @JsonProperty("number_of_profiles")
        Integer numberOfProfiles,

        @NotNull(message = "El tiempo promedio por día es obligatorio")
        @DecimalMin(value = "0.0", message = "El tiempo promedio debe ser mayor a 0")
        @DecimalMax(value = "24.0", message = "El tiempo promedio por día no puede exceder 24 horas")
        @Schema(
                description = "Promedio de horas vistas por día",
                example = "2.5",
                minimum = "0",
                maximum = "24"
        )
        @JsonProperty("avg_watch_time_per_day")
        Double avgWatchTimePerDay,

        @NotNull(message = "El método de pago es obligatorio")
        @JsonProperty("payment_method")
        @Schema(
                description = "Método de pago",
                example = "Credit Card",
                allowableValues = {
                        "Credit Card",
                        "Debit Card",
                        "PayPal",
                        "Gift Card",
                        "Crypto"
                }
        )
        PaymentMethod paymentMethod
) {
    // Validación cruzada Plan vs Precio
    @AssertTrue(message = "La tarifa mensual no coincide con el plan seleccionado")
    @Schema(hidden = true)
    private boolean isPlanPriceConsistent() {
        if (subscriptionType == null || monthlyFee == null) return true;
        return Math.abs(monthlyFee - subscriptionType.getPrice()) < 0.01;
    }
}
