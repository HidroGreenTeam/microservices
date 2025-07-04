package com.hidrogreen.treatment_service.diagnosis.interfaces.rest.transform;

import com.hidrogreen.treatment_service.diagnosis.domain.model.aggregates.Diagnosis;
import com.hidrogreen.treatment_service.diagnosis.interfaces.rest.resources.DiagnosisResource;


public class DiagnosisResourceFromEntityAssembler {

    public static DiagnosisResource toResourceFromEntity(Diagnosis entity) {
        return new DiagnosisResource(
            entity.getId(),
            entity.getCropId(),
            entity.getImageUrl(),
            entity.getStatus().name(),
            entity.getDetectionResult() != null ? entity.getDetectionResult().getDiseaseName() : null,
            entity.hasDiseaseDetected(),
            entity.getConfidenceScore(),
            entity.getDetectionResult() != null ? entity.getDetectionResult().recommendations() : null,
            entity.getAnalyzedAt(),
            entity.getNotes()
        );
    }
} 