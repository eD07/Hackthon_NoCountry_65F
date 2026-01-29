package com.hackathon.churninsight.controller;

import com.hackathon.churninsight.dto.response.RiskFactorsDTO;
import com.hackathon.churninsight.service.RiskFactorsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk-factors")
@RequiredArgsConstructor
@Tag(
        name = "Risk Factors",
        description = "Explainability and suggested actions for churn prediction"
)
public class RiskFactorsController {

    private final RiskFactorsService riskFactorsService;

    @GetMapping("/{customerId}")
    @Operation(
            summary = "Explain churn risk for a customer",
            description = "Returns risk reasons, suggested action and similar users count based on the latest prediction"
    )
    public RiskFactorsDTO explainCustomerRisk(@PathVariable String customerId) {
        return riskFactorsService.explainRisk(customerId);
    }
}