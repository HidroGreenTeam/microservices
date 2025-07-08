package com.ayni.crop_service.crops.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Crop resource
 */
public record CropResource(
    Long id,
    String cropName,
    Double area,
    LocalDate plantingDate,
    String location,
    Long farmerId,
    String imageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
