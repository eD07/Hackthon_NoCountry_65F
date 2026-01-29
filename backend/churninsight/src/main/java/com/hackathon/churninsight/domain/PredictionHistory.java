package com.hackathon.churninsight.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    private String subscriptionType;
    private String paymentMethod;
    private Double monthlyFee;

    @Column(name = "watch_hours", nullable = false)
    private Double watchHours;

    @Column(name = "last_login_days", nullable = false)
    private Integer lastLoginDays;

    @Column(name = "number_of_profiles", nullable = false)
    private Integer numberOfProfiles;

    @Column(name = "avg_watch_time_per_day", nullable = false)
    private Double avgWatchTimePerDay;

    private Double probability;

    @Column(name = "label", nullable = false)
    private String label; // will_churn / no_churn

    @Column(name = "prediction_label")
    private String predictionLabel; // Riesgo alto / medio / bajo

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
    }
}
