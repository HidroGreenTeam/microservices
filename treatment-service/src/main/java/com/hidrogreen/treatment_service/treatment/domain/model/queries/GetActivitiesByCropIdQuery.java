package com.hidrogreen.treatment_service.treatment.domain.model.queries;

/**
 * Query to get activities by crop ID
 */
public record GetActivitiesByCropIdQuery(Long cropId) {
    public GetActivitiesByCropIdQuery {
        if (cropId == null || cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be null or negative");
        }
    }

    public Long getCropId() {
        return cropId;
    }
}
