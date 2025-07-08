package com.ayni.crop_service.crops.domain.services;

import java.util.Optional;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.DeleteCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropImageCommand;

/**
 * Crop command service interface
 */
public interface CropCommandService {
    /**
     * Create a new crop
     *
     * @param command the create crop command
     * @return the created crop ID
     */
    Long handle(CreateCropCommand command);

    /**
     * Update a crop
     *
     * @param command the update crop command
     * @return the updated crop
     */
    Optional<Crop> handle(UpdateCropCommand command);

    /**
     * Delete a crop
     *
     * @param command the delete crop command
     */
    void handle(DeleteCropCommand command);

    /**
     * Update crop image
     *
     * @param command the update crop image command
     * @return the updated crop
     */
    Optional<Crop> handle(UpdateCropImageCommand command);

    /**
     * Remove crop image
     *
     * @param cropId the crop ID
     * @return the updated crop
     */
    Optional<Crop> removeCropImage(Long cropId);
}
