package com.hidrogreen.subscription_service.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ServiceAuthorizationFilter serviceAuthorizationFilter;

    public SecurityConfig(ServiceAuthorizationFilter serviceAuthorizationFilter) {
        this.serviceAuthorizationFilter = serviceAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api/v1/health", "/actuator/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(serviceAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
