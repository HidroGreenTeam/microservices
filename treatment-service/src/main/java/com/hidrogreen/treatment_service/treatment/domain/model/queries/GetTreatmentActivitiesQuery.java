package com.hidrogreen.treatment_service.treatment.domain.model.queries;


public record GetTreatmentActivitiesQuery(
    Long cropId,
    Long treatmentId
) {
    public GetTreatmentActivitiesQuery {
        if (cropId != null && cropId <= 0) {
            throw new IllegalArgumentException("Crop ID cannot be negative");
        }
        if (treatmentId != null && treatmentId <= 0) {
            throw new IllegalArgumentException("Treatment ID cannot be negative");
        }
    }

    public Long getCropId() {
        return cropId;
    }

    public Long getTreatmentId() {
        return treatmentId;
    }
}
