package com.ayni.crop_service.crops.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Crop resource
 */
public record CropResource(
    Long id,
    Long profileId,
    String cropName,
    LocalDate plantingDate,
    String location,
    String healthStatus,
    String notes,
    String createdAt,
    String updatedAt
) {}
