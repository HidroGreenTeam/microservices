package com.hidrogreen.user_service.profiles.interfaces.rest.transform;

import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import com.hidrogreen.user_service.profiles.interfaces.rest.resources.FarmerResource;

public class FarmerResourceFromEntityAssembler {
    public static FarmerResource toResourceFromEntity(Farmer farmer) {
        return new FarmerResource(
                farmer.getId(),
                farmer.getUsername(),
                farmer.getEmail(),
                farmer.getPhoneNumber(),
                farmer.getPassword(),
                farmer.getFarmerImage() != null ? farmer.getFarmerImage().getImageUrl() : null // Aquí evitamos el NullPointerException

        );
    }
}
