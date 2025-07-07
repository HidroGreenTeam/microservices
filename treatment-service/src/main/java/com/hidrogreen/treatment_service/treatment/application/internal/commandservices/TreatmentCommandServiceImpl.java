package com.hidrogreen.treatment_service.treatment.application.internal.commandservices;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;
import com.hidrogreen.treatment_service.treatment.domain.services.TreatmentCommandService;
import com.hidrogreen.treatment_service.treatment.domain.exceptions.TreatmentNotFoundException;
import com.hidrogreen.treatment_service.treatment.infrastructure.persistence.jpa.repositories.TreatmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TreatmentCommandServiceImpl implements TreatmentCommandService {
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
        Treatment treatment = treatmentRepository.findById(treatmentId)
            .orElseThrow(() -> new TreatmentNotFoundException(treatmentId));

        TreatmentStep step = new TreatmentStep(treatment, name, description, 
                                             scheduledDate, hasReminder, 
                                             reminderMinutesBefore);
        treatment.addStep(step);
        return treatmentRepository.save(treatment);
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