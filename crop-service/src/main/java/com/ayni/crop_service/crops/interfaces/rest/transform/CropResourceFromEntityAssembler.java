package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.interfaces.rest.resources.CropResource;

public class CropResourceFromEntityAssembler {
    public static CropResource toResourceFromEntity(Crop crop) {
        return new CropResource(
                crop.getId(),
                crop.getCropName(),
                crop.getIrrigationType().name(),
                crop.getArea(),
                crop.getPlantingDate().toString(),
                crop.getFarmerId(),
                crop.getCropImage() != null ? crop.getCropImage().getImageUrl() : null // Aquí evitamos el NullPointerException
        );
    }

}