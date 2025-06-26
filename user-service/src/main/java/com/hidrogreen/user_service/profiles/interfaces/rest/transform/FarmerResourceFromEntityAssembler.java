package com.hidrogreen.user_service.profiles.interfaces.rest.transform;

import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import com.hidrogreen.user_service.profiles.interfaces.rest.resources.FarmerResource;

public class FarmerResourceFromEntityAssembler {
    public static FarmerResource toResourceFromEntity(Farmer farmer) {
        return new FarmerResource(
                farmer.getId(),
                farmer.getUserId(),
                farmer.getFullName(),
                farmer.getPhoneNumber(),
                farmer.getAddress(),
                farmer.getFarmerImage() != null ? farmer.getFarmerImage().getImageUrl() : null
        );
    }
}
