package com.ayni.crop_service.crops.domain.model.commands;

import java.time.LocalDate;

/**
 * Command to update a crop
 */
public record UpdateCropCommand(
    Long cropId,
    String cropName,
    Double area,
    LocalDate plantingDate,
    String location
) {
   
}
