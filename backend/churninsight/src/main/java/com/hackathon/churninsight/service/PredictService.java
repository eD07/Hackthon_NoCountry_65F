package com.hackathon.churninsight.service;

import com.hackathon.churninsight.dto.request.PredictRequestDTO;
import com.hackathon.churninsight.dto.response.PredictResponseDTO;

/**
 * Servicio para realizar predicciones de churn de clientes.
 * Encapsula la lógica de comunicación con el servicio de Machine Learning
 * externo.
 */
public interface PredictService {

    /**
     * Realiza una predicción de churn para un cliente específico.
     *
     * @param request Datos del cliente y sus características
     * @return Respuesta con la predicción de churn y probabilidad
     * @throws com.hackathon.churninsight.exception.ExternalServiceException si hay error en el servicioML*/
    PredictResponseDTO predict(PredictRequestDTO request);
}
