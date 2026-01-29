package com.hackathon.churninsight.service.impl;

import com.hackathon.churninsight.domain.PredictionHistory;
import com.hackathon.churninsight.service.RecommendationService;
import org.springframework.stereotype.Service;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Override
    public String generateSuggestion(PredictionHistory h) {

        String risk = h.getPredictionLabel();

        if ("Riesgo alto".equalsIgnoreCase(risk)) {

            if (h.getLastLoginDays() >= 40) {
                return "Reenganche urgente: el cliente lleva más de "
                        + h.getLastLoginDays()
                        + " días sin acceder. Enviar notificación push con contenido personalizado.";
            }

            if ("Crypto".equalsIgnoreCase(h.getPaymentMethod())) {
                return "Retención crítica: ofrecer migración a débito o PayPal con beneficio temporal.";
            }

            return "Contacto directo: encuesta de satisfacción y oferta personalizada.";
        }

        if ("Riesgo medio".equalsIgnoreCase(risk)) {
            return "Incentivo: recomendar contenido destacado para aumentar horas vistas y evitar caída de engagement.";
        }

        if ("Riesgo bajo".equalsIgnoreCase(risk)) {
            return "Fidelización: invitar al programa de beneficios o referidos por alta actividad.";
        }

        return "Seguimiento estándar.";
    }
}

