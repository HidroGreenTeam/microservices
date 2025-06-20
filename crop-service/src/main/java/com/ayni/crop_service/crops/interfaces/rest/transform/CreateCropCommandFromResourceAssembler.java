package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.interfaces.rest.resources.CreateCropResource;

/**
 * Assembler to convert CreateCropResource to CreateCropCommand
 */
public class CreateCropCommandFromResourceAssembler {

    public static CreateCropCommand toCommandFromResource(CreateCropResource resource) {
        return new CreateCropCommand(
            resource.profileId(),
            resource.cropName(),
            resource.plantingDate(),
            resource.location(),
            resource.notes()
        );
    }
}
