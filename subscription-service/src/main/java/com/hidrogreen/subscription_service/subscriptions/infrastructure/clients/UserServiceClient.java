package com.hidrogreen.subscription_service.subscriptions.infrastructure.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(
    name = "user-service", 
    configuration = com.hidrogreen.subscription_service.shared.infrastructure.config.FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}")
    UserResponse getUserById(@PathVariable("userId") Long userId);

    
    record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName
    ) {}
} 