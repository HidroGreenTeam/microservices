package com.hidrogreen.treatment_service.treatment.application.internal.eventhandlers;

import com.hidrogreen.treatment_service.shared.domain.model.events.DiagnosisCompletedEvent;
import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.TreatmentActivity;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.ActivityRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class DiseaseDetectedEventHandler {

    private static final Logger log = Logger.getLogger(DiseaseDetectedEventHandler.class.getName());
    private final ActivityRepository activityRepository;

    public DiseaseDetectedEventHandler(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @EventListener
    public void on(DiagnosisCompletedEvent event) {
        
        if (!event.isDiseaseDetected()) {
            log.info("No disease detected in diagnosis " + event.getDiagnosisId() + ", skipping treatment activity creation");
            return;
        }
        
        Long cropId = event.getCropId();
        Long diagnosisId = event.getDiagnosisId();
        String detectedDisease = event.getDetectedDisease();
        
        log.info("Creating treatment activity for crop " + cropId + " - Disease: " + detectedDisease + 
                " (Confidence: " + String.format("%.1f", event.getConfidenceScore() * 100) + "%)");
        
        String title = String.format("Tratamiento para %s", detectedDisease);
        String description = String.format(
            "Tratamiento generado para %s detectada con confianza del %.1f%%",
            detectedDisease, event.getConfidenceScore() * 100
        );

        
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0);

        try {
            TreatmentActivity treatmentActivity = new TreatmentActivity(
                cropId,
                diagnosisId,
                title,
                description,
                detectedDisease,
                scheduledAt,
                "INITIAL"
            );

            TreatmentActivity saved = activityRepository.save(treatmentActivity);
            
            log.info("Successfully created treatment activity " + saved.getId() + 
                    " for crop " + cropId + " with diagnosis " + diagnosisId);
                
        } catch (Exception e) {
            log.severe("Failed to create treatment activity for crop " + cropId + 
                      " with diagnosis " + diagnosisId + ": " + e.getMessage());
        }
    }
} 