package com.hackathon.churninsight.service.impl;

import com.hackathon.churninsight.domain.PredictionHistory;
import com.hackathon.churninsight.dto.response.KPIsDTO;
import com.hackathon.churninsight.repository.PredictionHistoryRepository;
import com.hackathon.churninsight.service.KPIsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KPIsServiceImpl implements KPIsService {

    private final PredictionHistoryRepository historyRepo;

    @Override
    public KPIsDTO getKPIs() {
        List<PredictionHistory> latest = historyRepo.findLatestPerCustomer();

        long total = latest.size();
        long high = latest.stream()
                .filter(h -> "Riesgo alto".equalsIgnoreCase(h.getPredictionLabel()))
                .count();
        long medium = latest.stream()
                .filter(h -> "Riesgo medio".equalsIgnoreCase(h.getPredictionLabel()))
                .count();
        long low = latest.stream()
                .filter(h -> "Riesgo bajo".equalsIgnoreCase(h.getPredictionLabel()))
                .count();

        // Tasa de churn
        double churnRate = total > 0 ? (double) latest.stream()
                .filter(h -> "will_churn".equalsIgnoreCase(h.getLabel()))
                .count() / total * 100 : 0.0;

        return new KPIsDTO(total, high, medium, low, churnRate);
    }
}
