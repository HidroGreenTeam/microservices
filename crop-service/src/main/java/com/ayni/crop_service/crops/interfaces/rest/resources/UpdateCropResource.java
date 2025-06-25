package com.ayni.crop_service.crops.interfaces.rest.resources;

import com.ayni.crop_service.crops.domain.model.valueobjects.IrrigationType;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateCropResource(
        @NotBlank(message = "Crop name is mandatory")
        String cropName,
        IrrigationType irrigationType,
        Long area,
        LocalDate plantingDate,
        Long farmerId
) {
}
