package com.hackathon.churninsight.service.impl;

import com.hackathon.churninsight.domain.PredictionHistory;
import com.hackathon.churninsight.dto.response.PredictionHistoryDTO;
import com.hackathon.churninsight.repository.PredictionHistoryRepository;
import com.hackathon.churninsight.service.PredictionHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PredictionHistoryServiceImpl implements PredictionHistoryService {

    private final PredictionHistoryRepository repo;

    public PredictionHistoryServiceImpl(PredictionHistoryRepository repo) {
        this.repo = repo;
    }

    // Método para cargar el historial paginado
    @Override
    public List<PredictionHistoryDTO> latest(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PredictionHistory> historyPage = repo.findAll(pageRequest);

        return historyPage.stream()
                .map(this::toDto)
                .toList();
    }

    // Método para filtrar por Cliente
    @Override
    public List<PredictionHistoryDTO> byCustomer(String customerId, int page, int size) {
        // Usamos PageRequest para la paginación
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // Recuperamos la página de resultados y mapeamos a DTO
        Page<PredictionHistory> historyPage = repo.findByCustomerId(customerId, pageRequest);
        return historyPage.stream()
                .map(this::toDto)
                .toList();
    }

    // Método para filtrar por fecha
    @Override
    public List<PredictionHistoryDTO> byDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        // Usamos PageRequest para la paginación
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // Recuperamos la página de resultados filtrada por fecha
        Page<PredictionHistory> historyPage = repo.findByCreatedAtBetween(startDate, endDate, pageRequest);
        return historyPage.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void clearAll() {
        repo.deleteAll();
    }

    private PredictionHistoryDTO toDto(PredictionHistory h) {
        return new PredictionHistoryDTO(
                h.getId(),
                h.getCustomerId(),
                h.getSubscriptionType(),
                h.getPaymentMethod(),
                h.getMonthlyFee(),
                h.getWatchHours(),
                h.getLastLoginDays(),
                h.getNumberOfProfiles(),
                h.getAvgWatchTimePerDay(),
                h.getProbability(),
                h.getLabel(),
                h.getPredictionLabel(),
                h.getCreatedAt()
        );
    }
}
