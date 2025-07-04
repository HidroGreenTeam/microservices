package com.ayni.crop_service.crops.domain.model.commands;

import java.time.LocalDate;

public record UpdateCropCommand(
        Long id,
        String cropName,
        Long area, 
        LocalDate plantingDate,
        Long farmerId
) {
}
