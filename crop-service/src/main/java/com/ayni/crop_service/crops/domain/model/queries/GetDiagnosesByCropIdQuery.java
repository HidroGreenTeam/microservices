package com.ayni.crop_service.crops.domain.model.queries;

/**
 * Query to get diagnoses by crop ID
 */
public record GetDiagnosesByCropIdQuery(Long cropId) {
    public GetDiagnosesByCropIdQuery {
        if (cropId == null || cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be null or negative");
        }
    }

    public Long getCropId() {
        return cropId;
    }
}
