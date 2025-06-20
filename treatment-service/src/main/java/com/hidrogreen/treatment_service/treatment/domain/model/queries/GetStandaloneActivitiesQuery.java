package com.hidrogreen.treatment_service.treatment.domain.model.queries;

/**
 * Query to get standalone activities 🆓
 */
public record GetStandaloneActivitiesQuery(
    Long cropId,
    String createdByUser
) {
    public GetStandaloneActivitiesQuery {
        if (cropId != null && cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be negative");
        }
    }

    public Long getCropId() {
        return cropId;
    }
}
