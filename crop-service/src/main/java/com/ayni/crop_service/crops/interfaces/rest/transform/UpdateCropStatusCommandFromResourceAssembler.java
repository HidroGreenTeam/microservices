package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.commands.UpdateCropStatusCommand;
import com.ayni.crop_service.crops.domain.model.valueobjects.CropHealthStatus;
import com.ayni.crop_service.crops.interfaces.rest.resources.UpdateCropStatusResource;

/**
 * Assembler to convert UpdateCropStatusResource to UpdateCropStatusCommand
 */
public class UpdateCropStatusCommandFromResourceAssembler {

    public static UpdateCropStatusCommand toCommandFromResource(Long cropId, UpdateCropStatusResource resource) {
        return new UpdateCropStatusCommand(
            cropId,
            CropHealthStatus.valueOf(resource.healthStatus().toUpperCase()),
            resource.notes()
        );
    }
}
