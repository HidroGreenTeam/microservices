package com.ayni.crop_service.crops.application.internal.commandServiceImpl;

import com.ayni.crop_service.crops.client.FarmerClient;
import com.ayni.crop_service.crops.domain.model.aggregates.Crop;
import com.ayni.crop_service.crops.domain.model.commands.CreateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.DeleteCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateCropCommand;
import com.ayni.crop_service.crops.domain.model.commands.UpdateIrrigationTypeCommand;
import com.ayni.crop_service.crops.domain.model.entities.CropImage;
import com.ayni.crop_service.crops.domain.model.valueobjects.IrrigationType;
import com.ayni.crop_service.crops.domain.services.CropCommandService;
import com.ayni.crop_service.crops.domain.services.CropImageService;
import com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories.CropRepository;
import com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories.FarmerRegistryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class CropCommandServiceImpl implements CropCommandService {
    private final CropRepository cropRepository;
    private final FarmerRegistryRepository farmerRegistryRepository;
    private final CropImageService cropImageService;


    @Autowired
    public CropCommandServiceImpl(CropRepository cropRepository, FarmerClient farmerClient, FarmerRegistryRepository farmerRegistryRepository, CropImageService cropImageService) {
        this.cropRepository = cropRepository;
        this.farmerRegistryRepository = farmerRegistryRepository;
        this.cropImageService = cropImageService;
    }

    @Override
    @Transactional
    public Long handle(CreateCropCommand command, MultipartFile file) throws IOException {
        var name = command.cropName();
        var irrigationType = command.irrigationType();

        if (cropRepository.existsCropsByCropName(name)) {
            throw new IllegalArgumentException("Crop with name " + name + " already exists!!");
        }

        // Validar el tipo de riego
        if (!irrigationType.equals(IrrigationType.Manual) && !irrigationType.equals(IrrigationType.Automatic)) {
            throw new IllegalArgumentException("Irrigation type " + irrigationType + " is not valid!!");
        }

        // Validar que el agricultor exista
        if (!farmerRegistryRepository.existsById(command.farmerId())) {
            throw new IllegalArgumentException("Farmer with id " + command.farmerId() + " doesn't exist!!");
        }

        CropImage cropImage = null;
        if (file != null && !file.isEmpty()) {
            // Subir la imagen
            cropImage = cropImageService.uploadImage(file);
        }

        // Crear el cultivo
        Crop crop = new Crop(command);
        crop.setCropImage(cropImage); // Asignar la imagen al cultivo
        return cropRepository.save(crop).getId();
    }




    @Override
    @Transactional
    public Optional<Crop> handle(UpdateCropCommand command) {
        // buscamos en la bd si existe un cultivo con el id
        var crop = cropRepository.findById(command.id()).orElseThrow(() -> new IllegalArgumentException("Course with id " + command.id() + "doesn't exist!!"));

        var updatedCrop = cropRepository.save(crop.update(
                command.cropName(),
                command.irrigationType(),
                command.area(),
                command.plantingDate(),
                command.farmerId()
        ));


        return Optional.of(cropRepository.save(crop));
    }

    @Override
    @Transactional
    public Optional<Crop> handle(UpdateIrrigationTypeCommand command) {

        var crop = cropRepository.findById(command.id()).orElseThrow(() ->
                new IllegalArgumentException("Crop with id " + command.id() + " doesn't exist!!"));

        var irrigationType = command.irrigationType();

        // Validar el tipo de riego
        if (!irrigationType.equals(IrrigationType.Manual) && !irrigationType.equals(IrrigationType.Automatic)) {
            throw new IllegalArgumentException("Irrigation type " + irrigationType + " is not valid!!");
        }


        crop.setIrrigationType(irrigationType); // se actaliza

        return Optional.of(cropRepository.save(crop));
    }



    @Override
    @Transactional
    public void handle(DeleteCropCommand command) {
        // Verificar si el cultivo existe en la base de datos
        var crop = cropRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Crop with id " + command.id() + " doesn't exist!!"));

        // Si el cultivo tiene una imagen asociada, eliminar la imagen de Cloudinary
        if (crop.getCropImage() != null) {
            try {
                cropImageService.deleteImage(crop.getCropImage());
            } catch (IOException e) {
                throw new IllegalArgumentException("Error deleting image from Cloudinary for crop with id " + command.id());
            }
        }

        cropRepository.deleteById(command.id());
    }


    @Override
    @Transactional
    public Optional<Crop> UpdateCropImage(MultipartFile file, Crop crop) throws IOException {
        if (crop.getCropImage() != null) {
            cropImageService.deleteImage(crop.getCropImage());
        }

        // Subir la nueva imagen
        CropImage newImage = cropImageService.uploadImage(file);
        crop.setCropImage(newImage);

        return Optional.of(cropRepository.save(crop));
    }

    @Override
    @Transactional
    public Optional<Crop> deleteCropImage(Long cropId) throws IOException {
        // Verificar si el cultivo existe
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new IllegalArgumentException("Crop with id " + cropId + " doesn't exist!!"));

        // Verificar si el cultivo tiene una imagen
        if (crop.getCropImage() != null) {
            // Eliminar la imagen de Cloudinary y la referencia en la base de datos
            cropImageService.deleteImage(crop.getCropImage());
            crop.setCropImage(null);  // Eliminar la referencia a la imagen en el cultivo
            cropRepository.save(crop); // Guardar los cambios en la base de datos
        } else {
            throw new IllegalArgumentException("Crop with id " + cropId + " doesn't have an image.");
        }

        return Optional.of(crop);
    }



}
