package com.ayni.notification_service.shared.infrastructure.security.feign;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    private final ServiceTokenFeignInterceptor serviceTokenFeignInterceptor;

    public FeignConfiguration(ServiceTokenFeignInterceptor serviceTokenFeignInterceptor) {
        this.serviceTokenFeignInterceptor = serviceTokenFeignInterceptor;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return serviceTokenFeignInterceptor;
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
} 