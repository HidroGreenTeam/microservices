package com.hidrogreen.treatment_service.treatment.domain.services;

import com.hidrogreen.treatment_service.treatment.domain.model.aggregates.Treatment;

import java.time.LocalDateTime;

public interface TreatmentCommandService {
    Treatment createTreatment(Long diagnosisId, Long cropId, Long profileId, 
                            String diseaseType, Double confidence, 
                            String imageUrl, LocalDateTime diagnosisDate);
    
    Treatment addStep(Long treatmentId, String name, String description, 
                     LocalDateTime scheduledDate, boolean hasReminder, 
                     Integer reminderMinutesBefore);
    
    Treatment saveTreatment(Treatment treatment);
    
    Treatment startTreatment(Long treatmentId);
    
    Treatment completeTreatment(Long treatmentId);
    
    Treatment cancelTreatment(Long treatmentId);
} 