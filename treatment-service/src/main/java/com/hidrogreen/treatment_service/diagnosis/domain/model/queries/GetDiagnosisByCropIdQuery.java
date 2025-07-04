package com.hidrogreen.treatment_service.diagnosis.domain.model.queries;

/**
 * Query to get diagnosis by crop ID
 */
public record GetDiagnosisByCropIdQuery(Long cropId) {
    public GetDiagnosisByCropIdQuery {
        if (cropId == null || cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be null or negative");
        }
    }

    public Long getCropId() {
        return cropId;
    }
} 