package com.hackathon.churninsight.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SubscriptionType {

    BASIC("Basic", 8.99),
    STANDARD("Standard", 13.99),
    PREMIUM("Premium", 17.99);

    @JsonValue
    private final String jsonValue;

    private final double price;

    SubscriptionType(String jsonValue, double price) {
        this.jsonValue = jsonValue;
        this.price = price;
    }

    @JsonCreator
    public static SubscriptionType fromValue(String value) {
        for (SubscriptionType type : values()) {
            if (type.jsonValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de suscripción inválido: " + value);
    }
}

