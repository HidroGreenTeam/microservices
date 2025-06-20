package com.ayni.crop_service.crops.domain.model.commands;

/**
 * Command to start a diagnosis
 */
public record StartDiagnosisCommand(
    Long cropId,
    Long profileId,
    String imageUrl
) {
    public StartDiagnosisCommand {
        if (cropId == null || cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be null or negative");
        }
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("Profile ID cannot be null or negative");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("Image URL cannot be null or blank");
        }
    }

    public Long getCropId() {
        return cropId;
    }

    public Long getProfileId() {
        return profileId;
    }
}
