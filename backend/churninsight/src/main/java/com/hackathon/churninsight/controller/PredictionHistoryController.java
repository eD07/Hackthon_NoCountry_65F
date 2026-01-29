package com.hackathon.churninsight.controller;

import com.hackathon.churninsight.dto.response.PredictionHistoryDTO;
import com.hackathon.churninsight.service.PredictionHistoryService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class PredictionHistoryController {

    private final PredictionHistoryService service;

    public PredictionHistoryController(PredictionHistoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<PredictionHistoryDTO> latest(@RequestParam(defaultValue = "0") int page, 
                                              @RequestParam(defaultValue = "20") int size) {
        return service.latest(page, size);
    }

    @GetMapping("/{customerId}")
    public List<PredictionHistoryDTO> byCustomer(@PathVariable String customerId, 
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return service.byCustomer(customerId, page, size);
    }

    // Endpoint para filtrar por rango de fechas
    @GetMapping("/filter")
    public List<PredictionHistoryDTO> byDateRange(@RequestParam("startDate") String startDate, 
                                                  @RequestParam("endDate") String endDate,
                                                  @RequestParam(defaultValue = "0") int page, 
                                                  @RequestParam(defaultValue = "20") int size) {
        // Definimos el formato de fecha que el frontend enviará
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Parseamos las fechas de String a LocalDateTime
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);
        
        // Llamamos al servicio con las fechas y los parámetros de paginación
        return service.byDateRange(start, end, page, size);
    }

    @DeleteMapping
    public void clear() {
        service.clearAll();
    }
}
