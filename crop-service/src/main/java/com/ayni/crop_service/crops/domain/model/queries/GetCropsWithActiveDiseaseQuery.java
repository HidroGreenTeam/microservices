package com.ayni.crop_service.crops.domain.model.queries;

/**
 * Query to get crops with active diseases
 */
public record GetCropsWithActiveDiseaseQuery(Long profileId) {
    public GetCropsWithActiveDiseaseQuery {
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("Profile ID cannot be null or negative");
        }
    }

    public Long getProfileId() {
        return profileId;
    }
}
