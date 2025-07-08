package com.ayni.crop_service.crops.interfaces.rest.transform;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.interfaces.rest.resources.CropResource;

/**
 * Assembler to convert Crop entity to CropResource
 */
public class CropResourceFromEntityAssembler {

    public static CropResource toResourceFromEntity(Crop entity) {
        return new CropResource(
            entity.getId(),
            entity.getCropName(),
            entity.getArea(),
            entity.getPlantingDate(),
            entity.getLocation(),
            entity.getFarmerId(),
            entity.getImageUrl(),
            entity.getCreatedAt() != null ? 
                LocalDateTime.ofInstant(entity.getCreatedAt().toInstant(), ZoneId.systemDefault()) : null,
            entity.getUpdatedAt() != null ? 
                LocalDateTime.ofInstant(entity.getUpdatedAt().toInstant(), ZoneId.systemDefault()) : null
        );
    }
}
