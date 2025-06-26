package com.ayni.notification_service.notifications.application.internal.outboundservices.acl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * External Profile Service - Feign Client for User Service communication
 */
@Service
public class ExternalProfileService {
    
    private static final Logger log = LoggerFactory.getLogger(ExternalProfileService.class);
    
    private final UserServiceClient userServiceClient;

    public ExternalProfileService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    /**
     * Get profile email by farmerId
     *
     * @param farmerId the profile ID
     * @return the profile email address
     */
    public String getProfileEmail(Long farmerId) {
        log.debug("Retrieving email for farmerId: {}", farmerId);
        
        try {
            String email = userServiceClient.getFarmerEmail(farmerId);
            log.debug("Retrieved email: {} for farmerId: {}", email, farmerId);
            return email;
        } catch (Exception e) {
            log.warn("Failed to retrieve email for farmerId: {}, using fallback: {}", farmerId, e.getMessage());
            return "user@example.com"; // Fallback email
        }
    }

    /**
     * Get profile phone number by farmerId
     *
     * @param farmerId the profile ID
     * @return the profile phone number
     */
    public String getProfilePhoneNumber(Long farmerId) {
        log.debug("Retrieving phone for farmerId: {}", farmerId);
        
        try {
            String phone = userServiceClient.getFarmerPhone(farmerId);
            log.debug("Retrieved phone: {} for farmerId: {}", phone, farmerId);
            return phone;
        } catch (Exception e) {
            log.warn("Failed to retrieve phone for farmerId: {}, using fallback: {}", farmerId, e.getMessage());
            return "+1234567890"; // Fallback phone number
        }
    }

    /**
     * Get profile name by farmerId
     *
     * @param farmerId the profile ID
     * @return the profile full name
     */
    public String getProfileName(Long farmerId) {
        log.debug("Retrieving name for farmerId: {}", farmerId);
        
        try {
            String name = userServiceClient.getFarmerName(farmerId);
            log.debug("Retrieved name: {} for farmerId: {}", name, farmerId);
            return name;
        } catch (Exception e) {
            log.warn("Failed to retrieve name for farmerId: {}, using fallback: {}", farmerId, e.getMessage());
            return "Unknown User"; // Fallback name
        }
    }

    /**
     * Check if profile exists by farmerId
     *
     * @param farmerId the profile ID
     * @return true if profile exists
     */
    public boolean existsProfile(Long farmerId) {
        log.debug("Checking if farmerId exists: {}", farmerId);
        
        try {
            Boolean exists = userServiceClient.farmerExists(farmerId);
            log.debug("farmerId {} exists: {}", farmerId, exists);
            return exists;
        } catch (Exception e) {
            log.warn("Failed to check if farmerId exists: {}, assuming false: {}", farmerId, e.getMessage());
            return false; // Assume profile doesn't exist on error
        }
    }

    /**
     * Feign Client for User Service communication
     */
    @FeignClient(name = "user-service", configuration = com.ayni.notification_service.shared.infrastructure.security.feign.FeignConfiguration.class)
    public interface UserServiceClient {
        
        @GetMapping("/api/v1/farmers/{farmerId}/email")
        String getFarmerEmail(@PathVariable("farmerId") Long farmerId);
        
        @GetMapping("/api/v1/farmers/{farmerId}/phone")
        String getFarmerPhone(@PathVariable("farmerId") Long farmerId);
        
        @GetMapping("/api/v1/farmers/{farmerId}/name")
        String getFarmerName(@PathVariable("farmerId") Long farmerId);
        
        @GetMapping("/api/v1/user-profiles/internal/farmers/{farmerId}/exists")
        Boolean farmerExists(@PathVariable("farmerId") Long farmerId);
    }
}
