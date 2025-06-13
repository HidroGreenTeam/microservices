package com.hidrogreen.user_service.profiles.domain.services;

import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import com.hidrogreen.user_service.profiles.domain.model.commands.CreateFarmerCommand;
import com.hidrogreen.user_service.profiles.domain.model.commands.UpdateFarmerCommand;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface FarmerCommandService {

    Long createFarmer(CreateFarmerCommand command);
    Optional<Farmer> updateFarmer(UpdateFarmerCommand command);
    void deleteFarmer(Long farmerId);

    Optional<Farmer> UpdateFarmerImage(MultipartFile file, Farmer farmer) throws IOException;
    Optional<Farmer> deleteFarmerImage(Long farmerId) throws IOException;
}
