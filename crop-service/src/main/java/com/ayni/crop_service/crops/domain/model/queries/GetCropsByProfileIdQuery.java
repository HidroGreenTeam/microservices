package com.ayni.crop_service.crops.domain.model.queries;

/**
 * Query to get crops by profile ID
 */
public record GetCropsByProfileIdQuery(Long profileId) {
    public GetCropsByProfileIdQuery {
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("Profile ID cannot be null or negative");
        }
    }

    public Long getProfileId() {
        return profileId;
    }
}
