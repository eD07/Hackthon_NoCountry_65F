package com.hackathon.churninsight.controller;

import com.hackathon.churninsight.dto.response.PredictionHistoryDTO;
import com.hackathon.churninsight.service.PredictionHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionHistoryControllerTest {

    @Mock
    private PredictionHistoryService service;

    @InjectMocks
    private PredictionHistoryController controller;

    @Test
    @DisplayName("Debería retornar lista de predicciones recientes")
    void latestTest() {
        when(service.latest(0, 20)).thenReturn(Collections.emptyList());

        List<PredictionHistoryDTO> result = controller.latest(0, 20);

        assertNotNull(result);
        verify(service, times(1)).latest(0, 20);
    }

    @Test
    @DisplayName("Debería filtrar por customerId")
    void byCustomerTest() {
        String customerId = "cust123";
        when(service.byCustomer(eq(customerId), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        List<PredictionHistoryDTO> result = controller.byCustomer(customerId, 0, 20);

        assertNotNull(result);
        verify(service).byCustomer(customerId, 0, 20);
    }

    @Test
    @DisplayName("Debería parsear fechas correctamente y llamar al servicio de filtrado")
    void byDateRangeTest() {
        // Formato esperado: yyyy-MM-dd HH:mm:ss
        String startStr = "2023-01-01 10:00:00";
        String endStr = "2023-01-02 10:00:00";

        when(service.byDateRange(any(LocalDateTime.class), any(LocalDateTime.class), eq(0), eq(20)))
                .thenReturn(Collections.emptyList());

        List<PredictionHistoryDTO> result = controller.byDateRange(startStr, endStr, 0, 20);

        assertNotNull(result);
        verify(service).byDateRange(
                argThat(date -> date.getYear() == 2023 && date.getMonthValue() == 1),
                any(LocalDateTime.class),
                eq(0),
                eq(20)
        );
    }

    @Test
    @DisplayName("Debería llamar a borrar todo el historial")
    void clearTest() {
        doNothing().when(service).clearAll();

        controller.clear();

        verify(service, times(1)).clearAll();
    }
}