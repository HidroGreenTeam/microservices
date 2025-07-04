package com.hidrogreen.treatment_service.diagnosis.domain.model.commands;

import com.hidrogreen.treatment_service.diagnosis.domain.model.valueobjects.DetectionResult;

/**
 * Command to complete a diagnosis
 */
public record CompleteDiagnosisCommand(
    Long diagnosisId,
    String diseaseName,
    boolean diseaseDetected,
    Double confidenceScore,
    String recommendations
) {
    public CompleteDiagnosisCommand {
        if (diagnosisId == null || diagnosisId <= 0) {
            throw new IllegalArgumentException("Diagnosis ID cannot be null or negative");
        }
        if (confidenceScore != null && (confidenceScore < 0.0 || confidenceScore > 1.0)) {
            throw new IllegalArgumentException("Confidence score must be between 0.0 and 1.0");
        }
    }

    public Long getDiagnosisId() {
        return diagnosisId;
    }

    public DetectionResult getDetectionResult() {
        return new DetectionResult(diseaseName, diseaseDetected, recommendations);
    }
} 