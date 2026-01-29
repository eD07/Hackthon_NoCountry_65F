package com.hackathon.churninsight.controller;

import com.hackathon.churninsight.dto.response.KPIsDTO;
import com.hackathon.churninsight.service.KPIsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KPIsController {

    private final KPIsService kpisService;

    @GetMapping("/api/kpis")
    public KPIsDTO getKPIs() {
        return kpisService.getKPIs();
    }
}

