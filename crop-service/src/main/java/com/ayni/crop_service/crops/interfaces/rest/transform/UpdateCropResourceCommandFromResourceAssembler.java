package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.commands.UpdateCropCommand;
import com.ayni.crop_service.crops.interfaces.rest.resources.UpdateCropResource;

public class UpdateCropResourceCommandFromResourceAssembler {
    public static UpdateCropCommand toCommandFromResource(Long id, UpdateCropResource resource) {

        return new UpdateCropCommand(
                id,
                resource.cropName(),
                resource.area(),
                resource.plantingDate(),
                resource.farmerId()
        );
    }
}
