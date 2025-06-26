package com.hidrogreen.treatment_service.diagnosis.domain.model.commands;

import java.time.LocalDateTime;

public record CreateDiagnosisCommand(
        Long cropId,
        Long farmerId,
        String predictedClass,
        Double confidence,
        Boolean diseaseDetected,
        Boolean requiresTreatment,
        String imageFilename,
        String imageUrl,
        String detectionNotes,
        LocalDateTime detectionDate
) {
}
