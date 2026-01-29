package com.hackathon.churninsight.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KPIsDTO {
    private long totalEvaluated;
    private long highRisk;
    private long mediumRisk;
    private long lowRisk;
    private double churnRate;
}

