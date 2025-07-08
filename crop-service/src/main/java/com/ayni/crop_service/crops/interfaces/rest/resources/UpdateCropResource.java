package com.ayni.crop_service.crops.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Update crop resource
 */
public record UpdateCropResource(
    String cropName,
    Double area,
    LocalDate plantingDate,
    String location
) {}
