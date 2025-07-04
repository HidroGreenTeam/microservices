package com.ayni.crop_service.crops.domain.services;

import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.DeleteCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropCommand;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface CropCommandService {
    Long handle(CreateCropCommand command, MultipartFile file) throws IOException; 
    Optional<Crop> handle(UpdateCropCommand command); 
    void handle(DeleteCropCommand command); 

    Optional<Crop> UpdateCropImage(MultipartFile file, Crop crop) throws IOException;
    Optional<Crop> deleteCropImage(Long cropId) throws IOException;
}
