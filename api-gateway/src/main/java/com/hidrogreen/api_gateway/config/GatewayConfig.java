package com.hidrogreen.api_gateway.config;

import java.util.List;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("*"));
        corsConfig.setMaxAge(3600L);
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfig.setAllowedHeaders(List.of("*"));


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Health check route para el path raíz
                .route("health-check", r -> r.path("/")
                        .and().method("GET")
                        .uri("lb://user-service"))
                
                // Auth routes - agregando sign-in además de login
                .route("auth-login", r -> r.path("/api/v1/auth/login")
                        .and().method("POST")
                        .uri("lb://user-service"))
                .route("auth-sign-in", r -> r.path("/api/v1/auth/sign-in")
                        .and().method("POST")
                        .uri("lb://user-service"))
                .route("auth-register", r -> r.path("/api/v1/auth/register")
                        .and().method("POST")
                        .uri("lb://user-service"))
                .route("auth-refresh", r -> r.path("/api/v1/auth/refresh")
                        .and().method("POST")
                        .uri("lb://user-service"))
                .route("auth-logout", r -> r.path("/api/v1/auth/logout")
                        .and().method("POST")
                        .uri("lb://user-service"))
                // Ruta genérica para auth (captura cualquier endpoint no específico)
                .route("auth-fallback", r -> r.path("/api/v1/auth/**")
                        .uri("lb://user-service"))
                
                // User service routes
                .route("users", r -> r.path("/api/v1/users/**")
                        .uri("lb://user-service"))
                .route("farmers", r -> r.path("/api/v1/farmers/**")
                        .uri("lb://user-service"))
                .route("roles", r -> r.path("/api/v1/roles/**")
                        .uri("lb://user-service"))
                
                // Crop service routes
                .route("crops", r -> r.path("/api/v1/crops/**")
                        .uri("lb://crop-service"))
                .route("diagnosis", r -> r.path("/api/v1/diagnosis/**")
                        .uri("lb://crop-service"))
                
                // Treatment service routes
                .route("activities", r -> r.path("/api/v1/activities/**")
                        .uri("lb://treatment-service"))
                
                // Notification service routes
                .route("notifications", r -> r.path("/api/v1/notifications/**")
                        .uri("lb://notification-service"))
                .route("reminders", r -> r.path("/api/v1/reminders/**")
                        .uri("lb://notification-service"))
                
                // Report service routes
                .route("reports", r -> r.path("/api/v1/reports/**")
                        .uri("lb://report-service"))
                
                // Detection service routes
                .route("detections", r -> r.path("/api/v1/detections/**")
                        .uri("lb://detection-service"))
                
                .build();
    }
}
