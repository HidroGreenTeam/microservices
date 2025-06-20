package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.interfaces.rest.resources.CropResource;

/**
 * Assembler to convert Crop entity to CropResource
 */
public class CropResourceFromEntityAssembler {

    public static CropResource toResourceFromEntity(Crop entity) {
        return new CropResource(
            entity.getId(),
            entity.getProfileId(),
            entity.getCropName(),
            entity.getPlantingDate(),
            entity.getLocation(),
            entity.getHealthStatus().name(),
            entity.getNotes(),
            entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null,
            entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null
        );
    }
}
