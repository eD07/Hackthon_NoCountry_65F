package com.hackathon.churninsight.dto.response;

import java.time.LocalDateTime;

public record PredictionHistoryDTO(
        Long id,
        String customerId,
        String subscriptionType,
        String paymentMethod,
        Double monthlyFee,
        Double watchHours,
        Integer lastLoginDays,
        Integer numberOfProfiles,
        Double avgWatchTimePerDay,
        Double probability,
        String label,           // will_churn / wont_churn (tu columna "label")
        String predictionLabel, // Riesgo alto/medio/bajo (tu columna prediction_label)
        LocalDateTime createdAt
) {}
