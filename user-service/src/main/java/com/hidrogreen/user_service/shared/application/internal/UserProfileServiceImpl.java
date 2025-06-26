package com.hidrogreen.user_service.shared.application.internal;

import com.hidrogreen.user_service.iam.domain.model.aggregates.User;
import com.hidrogreen.user_service.iam.domain.model.commands.SignUpCommand;
import com.hidrogreen.user_service.iam.domain.model.entities.Role;
import com.hidrogreen.user_service.iam.domain.model.valueobjects.Roles;
import com.hidrogreen.user_service.iam.domain.services.UserCommandService;
import com.hidrogreen.user_service.iam.domain.services.UserQueryService;
import com.hidrogreen.user_service.profiles.domain.model.aggregates.Farmer;
import com.hidrogreen.user_service.profiles.domain.model.commands.CreateFarmerCommand;
import com.hidrogreen.user_service.profiles.domain.model.queries.GetFarmerByIdQuery;
import com.hidrogreen.user_service.profiles.domain.model.queries.GetFarmerByUserIdQuery;
import com.hidrogreen.user_service.profiles.domain.services.FarmerCommandService;
import com.hidrogreen.user_service.profiles.domain.services.FarmerQueryService;
import com.hidrogreen.user_service.shared.domain.model.commands.CreateFarmerUserCommand;
import com.hidrogreen.user_service.shared.domain.services.UserProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final FarmerCommandService farmerCommandService;
    private final FarmerQueryService farmerQueryService;

    public UserProfileServiceImpl(
            UserCommandService userCommandService,
            UserQueryService userQueryService,
            FarmerCommandService farmerCommandService,
            FarmerQueryService farmerQueryService
    ) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.farmerCommandService = farmerCommandService;
        this.farmerQueryService = farmerQueryService;
    }

    @Override
    @Transactional
    public Optional<User> createFarmerUser(CreateFarmerUserCommand command) {
        try {
            var farmerRole = new Role(Roles.ROLE_FARMER);
            var signUpCommand = new SignUpCommand(
                command.fullName(),
                command.email(),
                command.password(),
                List.of(farmerRole)
            );

            var userOpt = userCommandService.handle(signUpCommand);
            if (userOpt.isEmpty()) {
                return Optional.empty();
            }

            var user = userOpt.get();
            var createFarmerCommand = new CreateFarmerCommand(
                user.getId(),
                command.fullName(),
                command.phoneNumber(),
                command.address()
            );

            var farmerId = farmerCommandService.createFarmer(createFarmerCommand);
            if (farmerId == null) {
                return Optional.empty();
            }

            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public boolean deleteUserAndProfile(Long userId) {
        try {
            // Get user by id
            var userOpt = userQueryService.getUserById(userId);
            if (userOpt.isEmpty()) {
                return false;
            }
            
            var user = userOpt.get();
            
            // Delete the farmer profile (and its image) if exists
            var farmerOpt = getFarmerByUserId(userId);
            if (farmerOpt.isPresent()) {
                farmerCommandService.deleteFarmer(farmerOpt.get().getId());
            }
            
            // Hard delete the user
            userCommandService.delete(user);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<User> getUserByFarmerId(Long farmerId) {
        var farmerOpt = farmerQueryService.getFarmerById(new GetFarmerByIdQuery(farmerId));
        if (farmerOpt.isEmpty()) {
            return Optional.empty();
        }
        
        return userQueryService.getUserById(farmerOpt.get().getUserId());
    }

    @Override
    public Optional<Farmer> getFarmerByUserId(Long userId) {
        return farmerQueryService.getFarmerByUserId(new GetFarmerByUserIdQuery(userId));
    }

    @Override
    public boolean userExists(Long userId) {
        return userQueryService.getUserById(userId).isPresent();
    }
} 