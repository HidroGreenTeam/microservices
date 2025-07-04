package com.ayni.crop_service.crops.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateCropResource(
        @NotBlank(message = "Crop name is mandatory")
        String cropName,

        @NotNull(message = "Area is mandatory")
        Long area,

        @NotNull(message = "Planting date is mandatory")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate plantingDate
) {
}