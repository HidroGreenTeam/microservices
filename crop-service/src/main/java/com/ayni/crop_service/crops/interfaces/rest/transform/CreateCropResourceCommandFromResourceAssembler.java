package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.interfaces.rest.resources.CreateCropResource;

public class CreateCropResourceCommandFromResourceAssembler {
    
    
    public static CreateCropCommand toCommandFromResource(CreateCropResource resource, Long farmerId) {
        return new CreateCropCommand(
                resource.cropName(),
                resource.area(),
                resource.plantingDate(),
                farmerId
        );
    }
    
    
    @Deprecated
    public static CreateCropCommand toCommandFromResource(CreateCropResource resource) {
        throw new UnsupportedOperationException(
            "This method is deprecated. Use toCommandFromResource(CreateCropResource, Long farmerId) instead. " +
            "farmerId should now be passed as a path variable, not in the request body."
        );
    }
}
