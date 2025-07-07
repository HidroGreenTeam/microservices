package com.hidrogreen.treatment_service.treatment.application.internal.queryservices;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import com.hidrogreen.treatment_service.treatment.domain.model.valueobjects.TreatmentStatus;
import com.hidrogreen.treatment_service.treatment.domain.services.TreatmentQueryService;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.TreatmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TreatmentQueryServiceImpl implements TreatmentQueryService {
    private final TreatmentRepository treatmentRepository;

    public TreatmentQueryServiceImpl(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
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
        return treatmentRepository.findByStepsScheduledDateBeforeAndStepsStatusStatus(
            LocalDateTime.now(), 
            "PENDING"
        );
    }

    @Override
    public List<TreatmentStep> getStepsWithReminders() {
        return treatmentRepository.findByStepsHasReminderTrue();
    }
    
    @Override
    public List<TreatmentStep> getStepsDueForReminder() {
        // Get steps that have reminders enabled and are not completed
        return treatmentRepository.findByStepsHasReminderTrueAndStepsStatusStatusNot("COMPLETED");
    }
} 