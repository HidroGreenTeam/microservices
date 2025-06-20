package com.ayni.crop_service.crops.domain.services;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropStatusCommand;

import java.util.Optional;

/**
 * Crop command service interface
 */
public interface CropCommandService {    /**
     * Create a new crop
     *
     * @param command the create crop command
     * @return the created crop ID
     */
    Long handle(CreateCropCommand command);

    /**
     * Update crop status
     *
     * @param command the update crop status command
     * @return the updated crop
     */
    Optional<Crop> handle(UpdateCropStatusCommand command);

    /**
     * Update crop notes
     *
     * @param cropId the crop ID
     * @param notes the new notes
     * @return the updated crop
     */
    Optional<Crop> updateCropNotes(Long cropId, String notes);
}
