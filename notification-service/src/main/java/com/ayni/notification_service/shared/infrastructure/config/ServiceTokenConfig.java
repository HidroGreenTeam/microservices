package com.ayni.notification_service.shared.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ServiceTokenConfig {

    @Value("${service.internal.token}")
    private String internalToken;

    public String getInternalToken() {
        return internalToken;
    }

    public boolean isValidInternalToken(String token) {
        return internalToken != null && internalToken.equals(token);
    }
} 