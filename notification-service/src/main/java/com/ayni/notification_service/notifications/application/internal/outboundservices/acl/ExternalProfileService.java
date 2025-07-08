package com.ayni.notification_service.notifications.application.internal.outboundservices.acl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ayni.notification_service.shared.infrastructure.config.FeignClientConfig;

 
@FeignClient(
    name = "user-service", 
    configuration = FeignClientConfig.class
)
public interface ExternalProfileService {
    
     
    @GetMapping("/api/v1/farmers/{profileId}/email")
    String getProfileEmail(@PathVariable("profileId") Long profileId);
   
    @GetMapping("/api/v1/farmers/{profileId}/phone")
    String getProfilePhoneNumber(@PathVariable("profileId") Long profileId);
    
 
}
