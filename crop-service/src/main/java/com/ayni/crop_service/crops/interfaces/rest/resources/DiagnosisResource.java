package com.ayni.crop_service.crops.interfaces.rest.resources;

import java.time.LocalDateTime;

/**
 * Diagnosis resource
 */
public record DiagnosisResource(
    Long id,
    Long cropId,
    Long profileId,
    String imageUrl,
    String status,
    String detectedDisease,
    boolean diseaseDetected,
    Double confidenceScore,
    String recommendations,
    LocalDateTime analyzedAt,
    String notes
) {}
