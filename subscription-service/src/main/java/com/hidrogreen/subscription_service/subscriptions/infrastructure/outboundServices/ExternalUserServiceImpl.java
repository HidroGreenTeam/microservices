package com.hidrogreen.subscription_service.subscriptions.infrastructure.outboundServices;

import com.hidrogreen.subscription_service.subscriptions.application.internal.outboundServices.ExternalUserService;
import com.hidrogreen.subscription_service.subscriptions.infrastructure.clients.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExternalUserServiceImpl implements ExternalUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalUserServiceImpl.class);
    
    private final UserServiceClient userServiceClient;

    public ExternalUserServiceImpl(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Optional<UserInfo> getUserById(Long userId) {
        try {
            LOGGER.info("Fetching user information for user ID: {}", userId);
            
            
            UserServiceClient.UserResponse response = userServiceClient.getUserById(userId);
            
            if (response != null) {
                UserInfo userInfo = new UserInfo(
                    response.id(),
                    response.email(),
                    response.firstName(),
                    response.lastName()
                );
                LOGGER.info("Successfully fetched user information for user ID: {}", userId);
                return Optional.of(userInfo);
            }
            
            LOGGER.warn("User not found for ID: {}", userId);
            return Optional.empty();
            
        } catch (Exception e) {
            LOGGER.error("Failed to fetch user information for user ID: {}", userId, e);
            
            throw new RuntimeException("Error fetching user details for user ID: " + userId, e);
        }
    }
}
