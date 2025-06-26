package com.ayni.notification_service.shared.infrastructure.security.feign;

import com.ayni.notification_service.shared.infrastructure.security.jwt.JwtTokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class ServiceTokenFeignInterceptor implements RequestInterceptor {

    private final JwtTokenService jwtTokenService;
    private static final String SERVICE_NAME = "notification-service";

    public ServiceTokenFeignInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void apply(RequestTemplate template) {
        // Only add service token for internal endpoints
        if (template.url().contains("/internal/")) {
            String serviceToken = jwtTokenService.generateServiceToken(SERVICE_NAME);
            template.header("Authorization", "Bearer " + serviceToken);
        }
    }
} 