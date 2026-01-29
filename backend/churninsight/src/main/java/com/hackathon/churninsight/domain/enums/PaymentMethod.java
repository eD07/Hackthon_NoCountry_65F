package com.hackathon.churninsight.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PaymentMethod {

    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    PAYPAL("PayPal"),
    GIFT_CARD("Gift Card"),
    CRYPTO("Crypto");

    @JsonValue
    private final String jsonValue;

    PaymentMethod(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonCreator
    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : values()) {
            if (method.jsonValue.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Método de pago inválido: " + value);
    }
}
