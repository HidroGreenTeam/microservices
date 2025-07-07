package com.hidrogreen.treatment_service.treatment.domain.services;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;
import com.hidrogreen.treatment_service.treatment.domain.model.entities.TreatmentStep;

import java.util.List;
import java.util.Optional;

public interface TreatmentQueryService {
    Optional<Treatment> getTreatmentById(Long treatmentId);
    
    Optional<Treatment> getTreatmentByStepId(Long stepId);
    
    List<Treatment> getTreatmentsByCropId(Long cropId);
    
    List<Treatment> getTreatmentsByProfileId(Long profileId);
    
    List<Treatment> getOverdueTreatments();
    
    List<TreatmentStep> getOverdueSteps();
    
    List<TreatmentStep> getStepsWithReminders();
} 