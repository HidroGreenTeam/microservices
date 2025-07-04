package com.ayni.crop_service.shared.infrastructure.config;

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

    public static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service-Token";
    public static final String INTERNAL_SERVICE_USER = "system-internal";
} 