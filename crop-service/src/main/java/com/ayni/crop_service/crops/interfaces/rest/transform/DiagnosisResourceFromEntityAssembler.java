package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.aggregates.Diagnosis;
import com.ayni.crop_service.crops.interfaces.rest.resources.DiagnosisResource;

/**
 * Assembler to convert Diagnosis entity to DiagnosisResource
 */
public class DiagnosisResourceFromEntityAssembler {

    public static DiagnosisResource toResourceFromEntity(Diagnosis entity) {
        return new DiagnosisResource(
            entity.getId(),
            entity.getCropId(),
            entity.getProfileId(),
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
