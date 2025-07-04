package com.hidrogreen.treatment_service.shared.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceTokenConfig {

    @Value("${service.internal.token:HIDROGREEN_INTERNAL_SERVICE_TOKEN_2024}")
    private String internalServiceToken;

    @Bean
    public String internalServiceToken() {
        return internalServiceToken;
    }

    public static final String INTERNAL_SERVICE_HEADER = "X-Internal-Service-Token";
    public static final String INTERNAL_SERVICE_USER = "system-internal";
} 