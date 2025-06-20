package com.ayni.crop_service.crops.domain.model.commands;

import com.ayni.crop_service.crops.domain.model.valueobjects.CropHealthStatus;

/**
 * Command to update crop health status
 */
public record UpdateCropStatusCommand(
    Long cropId,
    CropHealthStatus healthStatus,
    String notes
) {
    public UpdateCropStatusCommand {
        if (cropId == null || cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be null or negative");
        }
        if (healthStatus == null) {
            throw new IllegalArgumentException("Health status cannot be null");
        }
    }

    public Long getCropId() {
        return cropId;
    }
}
