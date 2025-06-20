package com.ayni.crop_service.crops.domain.exceptions;

/**
 * Exception thrown when a crop is not found
 */
public class CropNotFoundException extends RuntimeException {
    public CropNotFoundException(Long cropId) {
        super("Crop with ID " + cropId + " not found");
    }
}
