package com.hackathon.churninsight.service;

import com.hackathon.churninsight.dto.response.RiskFactorsDTO;

public interface RiskFactorsService {
    RiskFactorsDTO explainRisk(String customerId);
}


