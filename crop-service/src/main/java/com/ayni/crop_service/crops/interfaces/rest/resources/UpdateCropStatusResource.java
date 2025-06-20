package com.ayni.crop_service.crops.interfaces.rest.resources;

/**
 * Resource for updating crop health status
 */
public record UpdateCropStatusResource(
    String healthStatus,
    String notes
) {}
