package com.hidrogreen.treatment_service.treatment.domain.model.queries;


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
