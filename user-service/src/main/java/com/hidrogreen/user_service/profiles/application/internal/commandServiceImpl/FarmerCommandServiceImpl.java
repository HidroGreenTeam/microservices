package com.hidrogreen.user_service.profiles.application.internal.commandServiceImpl;

import jakarta.transaction.Transactional;
import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import com.hidrogreen.user_service.profiles.domain.model.commands.CreateFarmerCommand;
import com.hidrogreen.user_service.profiles.domain.model.commands.UpdateFarmerCommand;
import com.hidrogreen.user_service.profiles.domain.model.entities.FarmerImage;
import com.hidrogreen.user_service.profiles.domain.model.events.FarmerCreatedEvent;
import com.hidrogreen.user_service.profiles.domain.services.FarmerCommandService;
import com.hidrogreen.user_service.profiles.domain.services.FarmerImageService;
import com.hidrogreen.user_service.profiles.infrastructure.persistence.jpa.repositories.FarmerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class FarmerCommandServiceImpl implements FarmerCommandService {

    private final FarmerRepository farmerRepository;
    private final ApplicationEventPublisher eventPublisher; // se usa para publicar eventos
    private final FarmerImageService farmerImageService;

    @Autowired
    public FarmerCommandServiceImpl(FarmerRepository farmerRepository, ApplicationEventPublisher applicationEventPublisher, ApplicationEventPublisher eventPublisher, FarmerImageService farmerImageService) {
        this.farmerRepository = farmerRepository;
        this.eventPublisher = eventPublisher;
        this.farmerImageService = farmerImageService;
    }

    @Override
    public Long createFarmer(CreateFarmerCommand command) {

        // Verificar si ya existe un Farmer con el mismo email
        if (farmerRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Farmer with email " + command.email() + " already exists");
        }

        Farmer farmer = new Farmer(command);
        farmerRepository.save(farmer);

        // Publicamos el evento
        FarmerCreatedEvent event = new FarmerCreatedEvent(this, farmer.getId(), farmer.getEmail());
        eventPublisher.publishEvent(event);

        return farmer.getId();
    }

    @Override
    public Optional<Farmer> updateFarmer(UpdateFarmerCommand command) {
        var farmer = farmerRepository.findById(command.farmerId()).orElseThrow(() -> new IllegalArgumentException("Farmer with id " + command.farmerId() + "doesn't exist!!"));

        if (command.username() != null && !command.username().isEmpty()) {
            farmer.setUsername(command.username());
        }

        if (command.phoneNumber() != null && !command.phoneNumber().isEmpty()) {
            farmer.setPhoneNumber(command.phoneNumber());
        }

        farmerRepository.save(farmer);

        return Optional.of(farmer);

    }

    @Override
    public void deleteFarmer(Long farmerId) {
        // Buscar el farmer
        var farmer = farmerRepository.findById(farmerId).orElseThrow(() -> new IllegalArgumentException("Farmer with id " + farmerId + "doesn't exist!!"));

        // verificar si el farmer tiene una imagen, eliminar de cloudinary
        if (farmer.getFarmerImage() != null) {
            try {
                farmerImageService.deleteImage(farmer.getFarmerImage());
            } catch (IOException e) {
                throw new IllegalArgumentException("Error while deleting farmer image: " + e.getMessage());
            }
        }

        farmerRepository.deleteById(farmerId);

    }

    @Override
    @Transactional
    public Optional<Farmer> UpdateFarmerImage(MultipartFile file, Farmer farmer) throws IOException {

        if (farmer.getFarmerImage() != null) {
            farmerImageService.deleteImage(farmer.getFarmerImage());
        }

        // Subir la imagen
        FarmerImage newImage = farmerImageService.uploadImage(file);
        farmer.setFarmerImage(newImage);

        return Optional.of(farmerRepository.save(farmer));
    }

    @Override
    public Optional<Farmer> deleteFarmerImage(Long farmerId) throws IOException {
        // Buscar el farmer
        Farmer farmer = farmerRepository.findById(farmerId).orElseThrow(() -> new IllegalArgumentException("Farmer with id " + farmerId + "doesn't exist!!"));

        // verificamos si el farmer tiene una imagen
        if (farmer.getFarmerImage() != null) {
            farmerImageService.deleteImage(farmer.getFarmerImage()); // eliminamos la imagen de cloudinary
            farmer.setFarmerImage(null); // eliminamos la imagen del farmer
            farmerRepository.save(farmer); // guardamos los cambios
        } else {
            throw new IllegalArgumentException("Farmer with id " + farmerId + " doesn't have an image!!");
        }
        return Optional.of(farmer);
    }
}
