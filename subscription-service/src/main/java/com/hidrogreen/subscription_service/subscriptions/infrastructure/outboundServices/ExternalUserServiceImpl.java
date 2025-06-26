package com.hidrogreen.subscription_service.subscriptions.infrastructure.outboundServices;

import com.hidrogreen.subscription_service.subscriptions.application.internal.outboundServices.ExternalUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ExternalUserServiceImpl implements ExternalUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalUserServiceImpl.class);
    
    @Value("${app.services.user-service.url:http://user-service}")
    private String userServiceUrl;
    
    private final RestTemplate restTemplate;

    public ExternalUserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<UserInfo> getUserById(Long userId) {
        try {
            LOGGER.info("Fetching user information for user ID: {}", userId);
            
            String url = userServiceUrl + "/api/v1/users/" + userId;
            
            // This would be the actual user response from user-service
            UserResponse response = restTemplate.getForObject(url, UserResponse.class);
            
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
            // Do not return a default user, instead throw an exception
            throw new RuntimeException("Error fetching user details for user ID: " + userId, e);
        }
    }
    
    private record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName
    ) {}
}
