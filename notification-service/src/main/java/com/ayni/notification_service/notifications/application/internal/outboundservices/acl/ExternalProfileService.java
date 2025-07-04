package com.ayni.notification_service.notifications.application.internal.outboundservices.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Service
public class ExternalProfileService {
    
    private static final Logger log = LoggerFactory.getLogger(ExternalProfileService.class);
    
    private final UserServiceClient userServiceClient;

    public ExternalProfileService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    
    public String getProfileEmail(Long profileId) {
        log.debug("Retrieving email for profileId: {}", profileId);
        
        try {
            String email = userServiceClient.getFarmerEmail(profileId);
            log.debug("Retrieved email: {} for profileId: {}", email, profileId);
            return email;
        } catch (Exception e) {
            log.warn("Failed to retrieve email for profileId: {}, using fallback: {}", profileId, e.getMessage());
            return "user@example.com"; 
        }
    }

    
    public String getProfilePhoneNumber(Long profileId) {
        log.debug("Retrieving phone for profileId: {}", profileId);
        
        try {
            String phone = userServiceClient.getFarmerPhone(profileId);
            log.debug("Retrieved phone: {} for profileId: {}", phone, profileId);
            return phone;
        } catch (Exception e) {
            log.warn("Failed to retrieve phone for profileId: {}, using fallback: {}", profileId, e.getMessage());
            return "+1234567890"; 
        }
    }

    
    public String getProfileName(Long profileId) {
        log.debug("Retrieving name for profileId: {}", profileId);
        
        try {
            String name = userServiceClient.getFarmerName(profileId);
            log.debug("Retrieved name: {} for profileId: {}", name, profileId);
            return name;
        } catch (Exception e) {
            log.warn("Failed to retrieve name for profileId: {}, using fallback: {}", profileId, e.getMessage());
            return "Unknown User"; 
        }
    }

    
    public boolean existsProfile(Long profileId) {
        log.debug("Checking if profileId exists: {}", profileId);
        
        try {
            Boolean exists = userServiceClient.farmerExists(profileId);
            log.debug("ProfileId {} exists: {}", profileId, exists);
            return exists;
        } catch (Exception e) {
            log.warn("Failed to check if profileId exists: {}, assuming false: {}", profileId, e.getMessage());
            return false; 
        }
    }

    
    @FeignClient(name = "user-service", configuration = com.ayni.notification_service.shared.infrastructure.config.FeignConfig.class)
    public interface UserServiceClient {
        
        @GetMapping("/api/v1/farmers/{farmerId}/email")
        String getFarmerEmail(@PathVariable("farmerId") Long farmerId);
        
        @GetMapping("/api/v1/farmers/{farmerId}/phone")
        String getFarmerPhone(@PathVariable("farmerId") Long farmerId);
        
        @GetMapping("/api/v1/farmers/{farmerId}/name")
        String getFarmerName(@PathVariable("farmerId") Long farmerId);
        
        @GetMapping("/api/v1/farmers/{farmerId}/exists")
        Boolean farmerExists(@PathVariable("farmerId") Long farmerId);
    }
}
