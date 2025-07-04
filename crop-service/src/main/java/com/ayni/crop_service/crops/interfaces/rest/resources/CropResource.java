package com.ayni.crop_service.crops.interfaces.rest.resources;

public record CropResource(
        Long id,
        String cropName,
        Long area,
        String plantingDate,
        Long farmerId,
        String imageUrl
) {
}