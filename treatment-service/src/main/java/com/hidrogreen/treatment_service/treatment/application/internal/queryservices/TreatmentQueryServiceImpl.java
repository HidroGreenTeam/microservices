package com.hidrogreen.treatment_service.treatment.application.internal.queryservices;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.TreatmentStatus;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.TreatmentStepStatus;
import com.hidrogreen.treatment_service.treatment.domain.services.TreatmentQueryService;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.TreatmentRepository;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.TreatmentStepRepository;

@Service
@Transactional(readOnly = true)
public class TreatmentQueryServiceImpl implements TreatmentQueryService {
    private static final Logger logger = LoggerFactory.getLogger(TreatmentQueryServiceImpl.class);
    
    private final TreatmentRepository treatmentRepository;
    private final TreatmentStepRepository treatmentStepRepository;

    public TreatmentQueryServiceImpl(TreatmentRepository treatmentRepository,
                                   TreatmentStepRepository treatmentStepRepository) {
        this.treatmentRepository = treatmentRepository;
        this.treatmentStepRepository = treatmentStepRepository;
        logger.info("TreatmentQueryServiceImpl initialized successfully");
    }

    @Override
    public Optional<Treatment> getTreatmentById(Long treatmentId) {
        return treatmentRepository.findById(treatmentId);
    }

    @Override
    public Optional<Treatment> getTreatmentByStepId(Long stepId) {
        return treatmentRepository.findByStepsId(stepId);
    }

    @Override
    public List<Treatment> getTreatmentsByCropId(Long cropId) {
        return treatmentRepository.findByCropId(cropId);
    }

    @Override
    public List<Treatment> getTreatmentsByProfileId(Long profileId) {
        return treatmentRepository.findByProfileId(profileId);
    }

    @Override
    public List<Treatment> getOverdueTreatments() {
        List<String> excludedStatuses = Arrays.asList(
            TreatmentStatus.Status.COMPLETED.toString(),
            TreatmentStatus.Status.CANCELLED.toString()
        );
        return treatmentRepository.findByEndDateBeforeAndStatusStatusNotIn(LocalDateTime.now(), excludedStatuses);
    }

    @Override
    public List<TreatmentStep> getOverdueSteps() {
        TreatmentStepStatus pendingStatus = new TreatmentStepStatus(TreatmentStepStatus.Status.PENDING);
        return treatmentStepRepository.findByScheduledDateBeforeAndStatus(
            LocalDateTime.now(), 
            pendingStatus
        );
    }

    @Override
    public List<TreatmentStep> getStepsWithReminders() {
        return treatmentStepRepository.findByHasReminderTrue();
    }
    
    @Override
    public List<TreatmentStep> getStepsDueForReminder() {
        logger.info("=== GETTING STEPS DUE FOR REMINDER ===");
        
        LocalDateTime now = LocalDateTime.now();
        logger.info("Current time: {}", now);
        
        // Buscar steps que tengan recordatorio habilitado y no estén completados
        logger.info("Searching for steps with reminders enabled and not completed...");
        
        try {
            // Usar la consulta con FETCH JOIN para evitar LazyInitializationException
            List<TreatmentStep> allStepsWithReminders = treatmentStepRepository.findByHasReminderTrueWithTreatment();
            logger.info("Found {} total steps with reminders enabled", allStepsWithReminders.size());
            
            List<TreatmentStep> dueSteps = new ArrayList<>();
            
            for (TreatmentStep step : allStepsWithReminders) {
                logger.info("Checking step ID: {} - {}", step.getId(), step.getName());
                logger.info("Step status: {}", step.getStatus());
                logger.info("Step scheduled date: {}", step.getScheduledDate());
                logger.info("Step reminder minutes before: {}", step.getReminderMinutesBefore());
                
                // Solo incluir steps que no estén completados ni saltados
                if (!step.getStatus().isCompleted() && !step.getStatus().isSkipped()) {
                    logger.info("Step {} is eligible for reminder checks", step.getId());
                    dueSteps.add(step);
                } else {
                    logger.info("Step {} is completed or skipped, skipping reminder check", step.getId());
                }
            }
            
            logger.info("=== STEPS DUE FOR REMINDER CHECK: {} ===", dueSteps.size());
            
            return dueSteps;
            
        } catch (Exception e) {
            logger.error("Error getting steps due for reminder", e);
            return Collections.emptyList();
        }
    }
}