package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.commands.UpdateCropCommand;
import com.ayni.crop_service.crops.interfaces.rest.resources.UpdateCropResource;

/**
 * Assembler to convert UpdateCropResource to UpdateCropCommand
 */
public class UpdateCropCommandFromResourceAssembler {

    public static UpdateCropCommand toCommandFromResource(Long cropId, UpdateCropResource resource) {
        return new UpdateCropCommand(
            cropId,
            resource.cropName(),
            resource.area(),
            resource.plantingDate(),
            resource.location()
        );
    }
}
