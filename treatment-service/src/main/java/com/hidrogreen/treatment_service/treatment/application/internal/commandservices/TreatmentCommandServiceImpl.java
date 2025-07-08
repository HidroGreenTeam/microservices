package com.hidrogreen.treatment_service.treatment.application.internal.commandservices;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hidrogreen.treatment_service.treatment.domain.exceptions.TreatmentNotFoundException;
import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import com.hidrogreen.treatment_service.treatment.domain.services.TreatmentCommandService;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.TreatmentRepository;

@Service
public class TreatmentCommandServiceImpl implements TreatmentCommandService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TreatmentCommandServiceImpl.class);
    
    private final TreatmentRepository treatmentRepository;

    public TreatmentCommandServiceImpl(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    @Override
    @Transactional
    public Treatment createTreatment(Long diagnosisId, Long cropId, Long profileId, 
                                   String diseaseType, Double confidence, 
                                   String imageUrl, LocalDateTime diagnosisDate) {
        Treatment treatment = new Treatment(diagnosisId, cropId, profileId, 
                                         diseaseType, confidence, imageUrl, diagnosisDate);
        return treatmentRepository.save(treatment);
    }

    @Override
    @Transactional
    public Treatment addStep(Long treatmentId, String name, String description, 
                           LocalDateTime scheduledDate, boolean hasReminder, 
                           Integer reminderMinutesBefore) {
        
        LOGGER.info("=== AÑADIENDO STEP AL TREATMENT ===");
        LOGGER.info("Treatment ID: {}", treatmentId);
        LOGGER.info("Step Name: {}", name);
        LOGGER.info("Scheduled Date: {}", scheduledDate);
        LOGGER.info("Has Reminder: {}", hasReminder);
        LOGGER.info("Reminder Minutes Before: {}", reminderMinutesBefore);
        
        try {
            Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> {
                    LOGGER.error("Treatment not found with ID: {}", treatmentId);
                    return new TreatmentNotFoundException(treatmentId);
                });

            LOGGER.info("Treatment found: {}", treatment.getId());
            LOGGER.info("Treatment Profile ID: {}", treatment.getProfileId());
            LOGGER.info("Treatment Disease Type: {}", treatment.getDiseaseType());

            TreatmentStep step = new TreatmentStep(treatment, name, description, 
                                                 scheduledDate, hasReminder, 
                                                 reminderMinutesBefore);
            
            LOGGER.info("TreatmentStep object created");
            LOGGER.info("Step Details - Name: {}, HasReminder: {}, ReminderMinutes: {}", 
                       step.getName(), step.isHasReminder(), step.getReminderMinutesBefore());
            
            treatment.addStep(step);
            LOGGER.info("Step added to treatment. Total steps now: {}", treatment.getSteps().size());
            
            Treatment savedTreatment = treatmentRepository.save(treatment);
            LOGGER.info("Treatment saved successfully with ID: {}", savedTreatment.getId());
            
            // Verificar el step guardado
            TreatmentStep savedStep = savedTreatment.getSteps().get(savedTreatment.getSteps().size() - 1);
            LOGGER.info("Saved step ID: {}", savedStep.getId());
            LOGGER.info("Saved step scheduled date: {}", savedStep.getScheduledDate());
            LOGGER.info("Saved step has reminder: {}", savedStep.isHasReminder());
            
            return savedTreatment;
        } catch (Exception e) {
            LOGGER.error("ERROR EN addStep: ", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Treatment saveTreatment(Treatment treatment) {
        return treatmentRepository.save(treatment);
    }

    @Override
    @Transactional
    public Treatment startTreatment(Long treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
            .orElseThrow(() -> new TreatmentNotFoundException(treatmentId));

        treatment.start();
        return treatmentRepository.save(treatment);
    }

    @Override
    @Transactional
    public Treatment completeTreatment(Long treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
            .orElseThrow(() -> new TreatmentNotFoundException(treatmentId));

        if (!treatment.isCompleted()) {
            throw new IllegalStateException("Treatment cannot be completed - not all steps are done");
        }

        treatment.complete();
        return treatmentRepository.save(treatment);
    }

    @Override
    @Transactional
    public Treatment cancelTreatment(Long treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
            .orElseThrow(() -> new TreatmentNotFoundException(treatmentId));

        treatment.cancel();
        return treatmentRepository.save(treatment);
    }
}
