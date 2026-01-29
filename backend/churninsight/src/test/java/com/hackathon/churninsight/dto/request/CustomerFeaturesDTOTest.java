package com.hackathon.churninsight.dto.request;

import com.hackathon.churninsight.domain.enums.PaymentMethod;
import com.hackathon.churninsight.domain.enums.SubscriptionType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerFeaturesDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debería ser válido cuando todos los campos son correctos")
    void validDtoTest() {
        // Asumiendo que SubscriptionType.PREMIUM.getPrice() devuelve 17.99
        CustomerFeaturesDTO dto = new CustomerFeaturesDTO(
                SubscriptionType.PREMIUM, 120.5, 3, 17.99, 4, 2.5, PaymentMethod.CREDIT_CARD
        );

        Set<ConstraintViolation<CustomerFeaturesDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Debería fallar si la tarifa mensual no coincide con el tipo de suscripción")
    void invalidPlanPriceConsistencyTest() {
        // Precio incorrecto para el plan Premium
        CustomerFeaturesDTO dto = new CustomerFeaturesDTO(
                SubscriptionType.PREMIUM, 100.0, 1, 5.0, 2, 1.0, PaymentMethod.PAYPAL
        );

        Set<ConstraintViolation<CustomerFeaturesDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v ->
                v.getMessage().equals("La tarifa mensual no coincide con el plan seleccionado")
        );
    }

    @Test
    @DisplayName("Debería fallar si el número de perfiles está fuera de rango")
    void numberOfProfilesRangeTest() {
        CustomerFeaturesDTO dto = new CustomerFeaturesDTO(
                SubscriptionType.BASIC, 10.0, 1, 9.99, 10, 1.0, PaymentMethod.DEBIT_CARD
        );

        Set<ConstraintViolation<CustomerFeaturesDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("numberOfProfiles")
        );
    }

    @Test
    @DisplayName("Debería fallar si los campos obligatorios son nulos")
    void nullFieldsTest() {
        CustomerFeaturesDTO dto = new CustomerFeaturesDTO(
                null, null, null, null, null, null, null
        );

        Set<ConstraintViolation<CustomerFeaturesDTO>> violations = validator.validate(dto);

        // Verifica que se capturen múltiples errores de @NotNull
        assertThat(violations.size()).isGreaterThanOrEqualTo(7);
    }
}
