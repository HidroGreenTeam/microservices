package com.ayni.crop_service.crops.application.internal.outboundservices.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for User Service - Direct communication with farmer REST endpoints
 */
@FeignClient(name = "user-service")
public interface ExternalProfileService {
    
    @GetMapping("/api/v1/farmers/{farmerId}/email")
    String getProfileEmailById(@PathVariable Long farmerId);
    
    @GetMapping("/api/v1/farmers/{farmerId}/phone")
    String getProfilePhoneNumberById(@PathVariable Long farmerId);
    
    @GetMapping("/api/v1/farmers/{farmerId}/name")
    String getProfileNameById(@PathVariable Long farmerId);
    
    @GetMapping("/api/v1/farmers/{farmerId}/exists")
    Boolean existsProfileById(@PathVariable Long farmerId);
}
