package com.hidrogreen.user_service.shared.domain.services;

import com.hidrogreen.user_service.iam.domain.model.aggregates.User;
import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import com.hidrogreen.user_service.shared.domain.model.commands.CreateFarmerUserCommand;

import java.util.Optional;

public interface UserProfileService {
    
    Optional<User> createFarmerUser(CreateFarmerUserCommand command);
    
    boolean deleteUserAndProfile(Long userId);
    
    Optional<User> getUserByFarmerId(Long farmerId);
    
    Optional<Farmer> getFarmerByUserId(Long userId);
    
    boolean userExists(Long userId);
} 