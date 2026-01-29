package com.hackathon.churninsight.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record RiskFactorsDTO(
        String riskLevel,
        List<String> riskFactors,
        String suggestedAction,
        long similarCustomersCount
) {}


