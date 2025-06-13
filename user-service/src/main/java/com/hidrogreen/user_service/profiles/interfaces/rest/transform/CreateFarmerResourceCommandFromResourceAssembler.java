package com.hidrogreen.user_service.profiles.interfaces.rest.transform;

import com.hidrogreen.user_service.profiles.domain.model.commands.CreateFarmerCommand;
import com.hidrogreen.user_service.profiles.interfaces.rest.resources.CreateFarmerResource;

public class CreateFarmerResourceCommandFromResourceAssembler {
    public static CreateFarmerCommand toCommandFromResource(CreateFarmerResource resource) {
        return new CreateFarmerCommand(
                resource.username(),
                resource.email(),
                resource.phoneNumber(),
                resource.password()
        );
    }
}
