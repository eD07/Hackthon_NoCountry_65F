package com.hackathon.churninsight.service.impl;

import com.hackathon.churninsight.domain.PredictionHistory;
import com.hackathon.churninsight.dto.response.PredictionHistoryDTO;
import com.hackathon.churninsight.repository.PredictionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionHistoryServiceImplTest {

    @Mock
    private PredictionHistoryRepository repo;

    @InjectMocks
    private PredictionHistoryServiceImpl service;

    private PredictionHistory mockHistory;

    @BeforeEach
    void setUp() {
        mockHistory = new PredictionHistory();
        mockHistory.setId(1L);
        mockHistory.setCustomerId("CUST-001");
        mockHistory.setProbability(0.85);
        mockHistory.setCreatedAt(LocalDateTime.now());
        // Rellenar otros campos si es necesario para evitar NullPointer en el toDto
        mockHistory.setSubscriptionType(null);
        mockHistory.setPaymentMethod(null);
    }

    @Test
    @DisplayName("Debería retornar lista de DTOs paginada al buscar los últimos registros")
    void latestTest() {
        Page<PredictionHistory> page = new PageImpl<>(List.of(mockHistory));
        when(repo.findAll(any(PageRequest.class))).thenReturn(page);

        List<PredictionHistoryDTO> result = service.latest(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CUST-001", result.get(0).customerId());
        verify(repo).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Debería filtrar por cliente y mapear correctamente a DTO")
    void byCustomerTest() {
        Page<PredictionHistory> page = new PageImpl<>(List.of(mockHistory));

        when(repo.findByCustomerId(eq("CUST-001"), any(PageRequest.class))).thenReturn(page);

        List<PredictionHistoryDTO> result = service.byCustomer("CUST-001", 0, 10);

        assertEquals(1, result.size());
        assertEquals(0.85, result.get(0).probability());
        verify(repo).findByCustomerId(eq("CUST-001"), any(PageRequest.class));
    }

    @Test
    @DisplayName("Debería filtrar por rango de fechas (Solución a Matchers)")
    void byDateRangeTest() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        Page<PredictionHistory> page = new PageImpl<>(List.of(mockHistory));

        when(repo.findByCreatedAtBetween(eq(start), eq(end), any(PageRequest.class)))
                .thenReturn(page);

        List<PredictionHistoryDTO> result = service.byDateRange(start, end, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repo).findByCreatedAtBetween(eq(start), eq(end), any(PageRequest.class));
    }

    @Test
    @DisplayName("Debería llamar al repositorio para eliminar todos los registros")
    void clearAllTest() {
        doNothing().when(repo).deleteAll();

        service.clearAll();

        verify(repo, times(1)).deleteAll();
    }
}