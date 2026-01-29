package com.hackathon.churninsight.service;

import com.hackathon.churninsight.dto.response.PredictionHistoryDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface PredictionHistoryService {
    List<PredictionHistoryDTO> latest(int page, int size);

    List<PredictionHistoryDTO> byCustomer(String customerId, int page, int size);

    List<PredictionHistoryDTO> byDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size); // Aquí

    void clearAll(); // Opcional

}
