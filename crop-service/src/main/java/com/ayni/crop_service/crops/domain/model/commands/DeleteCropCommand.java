package com.ayni.crop_service.crops.domain.model.commands;

/**
 * Command to delete a crop
 */
public record DeleteCropCommand(
    Long cropId
) {
   
}
