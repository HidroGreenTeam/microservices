package com.ayni.crop_service.crops.interfaces.rest.resources;

/**
 * Start diagnosis resource
 */
public record StartDiagnosisResource(
    Long cropId,
    Long profileId,
    String imageUrl
) {}
