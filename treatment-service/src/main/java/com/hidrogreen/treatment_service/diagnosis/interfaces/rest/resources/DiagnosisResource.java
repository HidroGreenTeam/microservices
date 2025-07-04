package com.hidrogreen.treatment_service.diagnosis.interfaces.rest.resources;

import java.time.LocalDateTime;


public record DiagnosisResource(
    Long id,
    Long cropId,
    String imageUrl,
    String status,
    String detectedDisease,
    boolean diseaseDetected,
    Double confidenceScore,
    String recommendations,
    LocalDateTime analyzedAt,
    String notes
) {} 