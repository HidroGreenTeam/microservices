package com.ayni.crop_service.crops.domain.model.commands;

import com.ayni.crop_service.crops.domain.model.valueobjects.IrrigationType;

import java.time.LocalDate;

public record CreateCropCommand(
        String cropName,
        IrrigationType irrigationType,
        Long area,
        LocalDate plantingDate,
        Long farmerId
) {
}
