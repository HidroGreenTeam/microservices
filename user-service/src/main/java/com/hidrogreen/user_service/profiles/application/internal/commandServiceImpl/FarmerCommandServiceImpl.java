package com.hidrogreen.user_service.profiles.application.internal.commandServiceImpl;

import jakarta.transaction.Transactional;
import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import com.hidrogreen.user_service.profiles.domain.model.commands.CreateFarmerCommand;
import com.hidrogreen.user_service.profiles.domain.model.commands.UpdateFarmerCommand;
import com.hidrogreen.user_service.profiles.domain.model.entities.FarmerImage;
import com.hidrogreen.user_service.profiles.domain.services.FarmerCommandService;
import com.hidrogreen.user_service.profiles.domain.services.FarmerImageService;
import com.hidrogreen.user_service.profiles.infrastructure.persistence.jpa.repositories.FarmerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class FarmerCommandServiceImpl implements FarmerCommandService {

    private final FarmerRepository farmerRepository;
    private final FarmerImageService farmerImageService;

    @Autowired
    public FarmerCommandServiceImpl(FarmerRepository farmerRepository, FarmerImageService farmerImageService) {
        this.farmerRepository = farmerRepository;
        this.farmerImageService = farmerImageService;
    }

    @Override
    public Long createFarmer(CreateFarmerCommand command) {
        if (farmerRepository.existsByUserId(command.userId())) {
            throw new RuntimeException("Farmer with userId " + command.userId() + " already exists");
        }

        Farmer farmer = new Farmer(command);
        farmerRepository.save(farmer);
        return farmer.getId();
    }

    @Override
    public Optional<Farmer> updateFarmer(UpdateFarmerCommand command) {
        var farmer = farmerRepository.findById(command.farmerId())
                .orElseThrow(() -> new RuntimeException("Farmer with id " + command.farmerId() + " doesn't exist"));

        if (command.fullName() != null && !command.fullName().isEmpty()) {
            farmer.setFullName(command.fullName());
        }

        if (command.phoneNumber() != null && !command.phoneNumber().isEmpty()) {
            farmer.setPhoneNumber(command.phoneNumber());
        }

        if (command.address() != null && !command.address().isEmpty()) {
            farmer.setAddress(command.address());
        }

        farmerRepository.save(farmer);
        return Optional.of(farmer);
    }

    @Override
    @Transactional
    public void deleteFarmer(Long farmerId) {
        Optional<Farmer> farmerOptional = farmerRepository.findById(farmerId);
        if (farmerOptional.isEmpty()) {
            throw new RuntimeException("Farmer with id " + farmerId + " doesn't exist");
        }
        
        Farmer farmer = farmerOptional.get();
        
        if (farmer.getFarmerImage() != null) {
            try {
                farmerImageService.deleteImage(farmer.getFarmerImage());
            } catch (IOException e) {
                throw new RuntimeException("Error while deleting farmer image: " + e.getMessage());
            }
        }
        
        farmerRepository.delete(farmer);
    }

    @Override
    public Optional<Farmer> UpdateFarmerImage(MultipartFile file, Farmer farmer) throws IOException {
        if (farmer.getFarmerImage() != null) {
            farmerImageService.deleteImage(farmer.getFarmerImage());
        }

        FarmerImage farmerImage = farmerImageService.uploadImage(file);
        farmer.setFarmerImage(farmerImage);
        farmerRepository.save(farmer);
        
        return Optional.of(farmer);
    }

    @Override
    public Optional<Farmer> deleteFarmerImage(Long farmerId) throws IOException {
        Optional<Farmer> farmerOptional = farmerRepository.findById(farmerId);
        
        if (farmerOptional.isEmpty()) {
            throw new RuntimeException("Farmer with id " + farmerId + " doesn't exist");
        }
        
        Farmer farmer = farmerOptional.get();
        
        if (farmer.getFarmerImage() != null) {
            farmerImageService.deleteImage(farmer.getFarmerImage());
            farmer.setFarmerImage(null);
            farmerRepository.save(farmer);
        } else {
            throw new RuntimeException("Farmer with id " + farmerId + " doesn't have an image");
        }
        
        return Optional.of(farmer);
    }
}
