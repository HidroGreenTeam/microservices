package com.hidrogreen.treatment_service.diagnosis.domain.services;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.diagnosis.domain.model.commands.CompleteDiagnosisCommand;

import java.util.Optional;


public interface DiagnosisCommandService {

    
    Optional<Diagnosis> handle(CompleteDiagnosisCommand command);

    
    Optional<Diagnosis> markAsFailed(Long diagnosisId, String reason);

    
    Optional<Diagnosis> getDiagnosisById(Long diagnosisId);
} 