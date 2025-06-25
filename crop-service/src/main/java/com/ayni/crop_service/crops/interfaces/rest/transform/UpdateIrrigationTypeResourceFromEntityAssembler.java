package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.commands.UpdateIrrigationTypeCommand;
import com.ayni.crop_service.crops.interfaces.rest.resources.UpdateIrrigationTypeResource;

public class UpdateIrrigationTypeResourceFromEntityAssembler {
    public static UpdateIrrigationTypeCommand toCommandFromResource(Long id, UpdateIrrigationTypeResource resource) {
        return new UpdateIrrigationTypeCommand(
                id,
                resource.irrigationType()
        );
    }
}

