package com.hackathon.churninsight.service.impl;

import com.hackathon.churninsight.domain.PredictionHistory;
import com.hackathon.churninsight.dto.response.RiskFactorsDTO;
import com.hackathon.churninsight.repository.PredictionHistoryRepository;
import com.hackathon.churninsight.service.RiskFactorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskFactorsServiceImpl implements RiskFactorsService {

    private final PredictionHistoryRepository historyRepository;

    @Override
    public RiskFactorsDTO explainRisk(String customerId) {

        // Obtener la última predicción del cliente
        PredictionHistory latest = historyRepository
                .findByCustomerId(
                        customerId,
                        PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"))
                )
                .getContent()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe historial de predicción para el cliente " + customerId
                        )
                );

        // Detectar factores de riesgo
        List<String> factors = detectRiskFactors(latest);

        // Generar acción sugerida
        String suggestedAction = generateSuggestedAction(latest);

        // Contar clientes con perfil similar (mismo riesgo)
        long similarCustomers = historyRepository.findAll().stream()
                .filter(h ->
                        h.getPredictionLabel() != null &&
                                h.getPredictionLabel().equalsIgnoreCase(latest.getPredictionLabel())
                )
                .map(PredictionHistory::getCustomerId)
                .distinct()
                .count();

        // Construir respuesta
        return new RiskFactorsDTO(
                latest.getPredictionLabel(),
                factors,
                suggestedAction,
                similarCustomers
        );
    }

    /* ==================== FACTORES DE RIESGO ========================== */
    private List<String> detectRiskFactors(PredictionHistory h) {

        List<String> factors = new ArrayList<>();

        if (h.getLastLoginDays() >= 40) {
            factors.add("No ha accedido a la plataforma en más de "
                    + h.getLastLoginDays() + " días");
        }

        if (h.getWatchHours() <= 5) {
            factors.add("Bajo nivel de consumo de contenido ("
                    + h.getWatchHours() + " horas totales)");
        }

        if (h.getAvgWatchTimePerDay() < 0.5) {
            factors.add("Muy bajo promedio de horas vistas por día ("
                    + h.getAvgWatchTimePerDay() + "h)");
        }

        if ("BASIC".equalsIgnoreCase(h.getSubscriptionType())) {
            factors.add("Plan Básico, asociado a mayor tasa de cancelación");
        }

        if ("CRYPTO".equalsIgnoreCase(h.getPaymentMethod())) {
            factors.add("Método de pago Crypto, con alta correlación histórica de churn");
        }

        // Casos positivos (riesgo bajo)
        if ("Riesgo bajo".equalsIgnoreCase(h.getPredictionLabel())) {

            if ("PREMIUM".equalsIgnoreCase(h.getSubscriptionType())) {
                factors.add("Suscripción Premium, asociada a alta retención");
            }

            if (h.getWatchHours() >= 20) {
                factors.add("Alto consumo de contenido ("
                        + h.getWatchHours() + " horas vistas)");
            }

            if (h.getLastLoginDays() <= 20) {
                factors.add("Acceso reciente a la plataforma");
            }
        }

        return factors;
    }

    /* ============================ ACCIÓN SUGERIDA ============================ */
    private String generateSuggestedAction(PredictionHistory h) {

        String risk = h.getPredictionLabel().toLowerCase();

        // Riesgo alto
        if (risk.contains("alto")) {

            if ("CRYPTO".equalsIgnoreCase(h.getPaymentMethod())) {
                return "Ofrecer migración a Débito o PayPal con un beneficio adicional para reducir fricción en el pago.";
            }

            if (h.getLastLoginDays() >= 40) {
                return "Enviar campaña de reactivación con recomendaciones personalizadas de contenido.";
            }

            return "Contacto proactivo con oferta de retención o cambio de plan.";
        }

        // Riesgo medio
        if (risk.contains("medio")) {

            if ("STANDARD".equalsIgnoreCase(h.getSubscriptionType())
                    && h.getWatchHours() < 10) {
                return "Sugerir contenido destacado para aumentar el tiempo de visualización.";
            }

            return "Ofrecer descuento por upgrade a plan anual.";
        }

        // Riesgo bajo
        if (risk.contains("bajo")) {

            if (!"PREMIUM".equalsIgnoreCase(h.getSubscriptionType())) {
                return "Ofrecer prueba gratuita del plan Premium por tiempo limitado.";
            }

            return "Incluir al cliente en programa de fidelización o referidos.";
        }

        return "Monitorear comportamiento del cliente en los próximos días.";
    }
}


