package com.ayni.crop_service.crops.domain.model.commands;

import java.time.LocalDate;

/**
 * Command to create a new crop
 */
public record CreateCropCommand(
    Long profileId,
    String cropName,
    LocalDate plantingDate,
    String location,
    String notes
) {
    public CreateCropCommand {
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("Profile ID cannot be null or negative");
        }
        if (cropName == null || cropName.trim().isEmpty()) {
            throw new IllegalArgumentException("Crop name cannot be null or empty");
        }
        if (plantingDate == null) {
            throw new IllegalArgumentException("Planting date cannot be null");
        }
        if (plantingDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Planting date cannot be in the future");
        }
    }

    public Long getProfileId() {
        return profileId;
    }
}
