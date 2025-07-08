package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.interfaces.rest.resources.CreateCropResource;

/**
 * Assembler to convert CreateCropResource to CreateCropCommand
 */
public class CreateCropCommandFromResourceAssembler {

    public static CreateCropCommand toCommandFromResource(Long farmerId, CreateCropResource resource) {
        return new CreateCropCommand(
            farmerId,
            resource.cropName(),
            resource.area(),
            resource.plantingDate(),
            resource.location()
        );
    }
}
