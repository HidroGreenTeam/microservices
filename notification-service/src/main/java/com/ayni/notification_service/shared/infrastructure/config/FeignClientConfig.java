package com.ayni.notification_service.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign Client Configuration
 * Handles common headers for service-to-service communication
 */
@Configuration
public class FeignClientConfig {
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Add common headers for service-to-service communication
                template.header("X-Service-Name", "notification-service");
                template.header("Content-Type", "application/json");
            }
        };
    }
}
