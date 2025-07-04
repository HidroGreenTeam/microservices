package com.hidrogreen.treatment_service.diagnosis.domain.services;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.diagnosis.domain.model.queries.GetDiagnosisByCropIdQuery;

import java.util.List;
import java.util.Optional;


public interface DiagnosisQueryService {

    
    Optional<Diagnosis> getDiagnosisById(Long diagnosisId);

    
    List<Diagnosis> handle(GetDiagnosisByCropIdQuery query);

    
    List<Diagnosis> getDiagnosisByProfileId(Long profileId);

    
    List<Diagnosis> getPendingDiagnosis();
} 