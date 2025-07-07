package com.hidrogreen.treatment_service.treatment.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.hidrogreen.treatment_service.treatment.domain.services.TreatmentCommandService;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Listener para mensajes de diagnóstico desde Detection Service
 */
@Component
public class DiagnosisMessageListener {

    private final TreatmentCommandService treatmentCommandService;

    public DiagnosisMessageListener(TreatmentCommandService treatmentCommandService) {
        this.treatmentCommandService = treatmentCommandService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.diagnosis}")
    public void handleDiagnosisMessage(Map<String, Object> message) {
        // Extraer datos del mensaje
        Long diagnosisId = Long.valueOf(message.get("diagnosis_id").toString());
        Long cropId = Long.valueOf(message.get("crop_id").toString());
        Long profileId = Long.valueOf(message.get("profile_id").toString());
        String diseaseType = (String) message.get("disease_detected");
        Double confidence = Double.valueOf(message.get("confidence").toString());
        String imageUrl = (String) message.get("image_url");
        LocalDateTime diagnosisDate = LocalDateTime.parse(message.get("analyzed_at").toString());

        // Crear el tratamiento
        treatmentCommandService.createTreatment(
            diagnosisId,
            cropId,
            profileId,
            diseaseType,
            confidence,
            imageUrl,
            diagnosisDate
        );
    }
}
