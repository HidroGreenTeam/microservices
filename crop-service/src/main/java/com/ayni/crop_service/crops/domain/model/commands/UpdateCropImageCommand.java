package com.ayni.crop_service.crops.domain.model.commands;

/**
 * Command to update crop image
 */
public record UpdateCropImageCommand(
    Long cropId,
    String imageUrl
) {
   
}
