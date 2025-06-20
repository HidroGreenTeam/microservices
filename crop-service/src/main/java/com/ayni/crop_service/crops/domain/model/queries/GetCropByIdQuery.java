package com.ayni.crop_service.crops.domain.model.queries;

/**
 * Query to get crop by ID
 */
public record GetCropByIdQuery(Long cropId) {
    public GetCropByIdQuery {
        if (cropId == null || cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be null or negative");
        }
    }

    public Long getCropId() {
        return cropId;
    }
}
