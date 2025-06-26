package com.ayni.crop_service.shared.infrastructure.security.feign;

import com.ayni.crop_service.shared.infrastructure.security.jwt.JwtTokenService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class ServiceTokenFeignInterceptor implements RequestInterceptor {

    private final JwtTokenService jwtTokenService;
    private static final String SERVICE_NAME = "crop-service";

    public ServiceTokenFeignInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void apply(RequestTemplate template) {
        // Generate service token for internal communication
        String serviceToken = jwtTokenService.generateServiceToken(SERVICE_NAME);
        template.header("Authorization", "Bearer " + serviceToken);
    }
} 