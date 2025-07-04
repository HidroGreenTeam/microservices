package com.ayni.crop_service.crops.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateCropResource(
        @NotBlank(message = "Crop name is mandatory")
        String cropName,
        Long area,
        LocalDate plantingDate,
        Long farmerId
) {
}
