package com.ayni.crop_service.crops.domain.model.commands;

import java.time.LocalDate;

public record CreateCropCommand(
        String cropName,
        Long area,
        LocalDate plantingDate,
        Long farmerId
) {
}
