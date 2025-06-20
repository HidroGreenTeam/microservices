package com.ayni.crop_service.crops.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Create crop resource
 */
public record CreateCropResource(
    Long profileId,
    String cropName,
    LocalDate plantingDate,
    String location,
    String notes
) {}
