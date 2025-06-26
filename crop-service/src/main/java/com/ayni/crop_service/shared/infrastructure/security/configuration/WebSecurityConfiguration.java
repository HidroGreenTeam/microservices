package com.ayni.crop_service.shared.infrastructure.security.configuration;

import com.ayni.crop_service.shared.infrastructure.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public WebSecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("""
                {
                    "error": "Unauthorized",
                    "message": "Authentication required to access this resource",
                    "path": "%s"
                }
                """.formatted(request.getRequestURI()));
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CORS Configuration
        http.cors(configurer -> configurer.configurationSource(source -> {
            var cors = new CorsConfiguration();
            cors.setAllowedOrigins(List.of("*"));
            cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
            cors.setAllowedHeaders(List.of("*"));
            return cors;
        }));

        // CSRF disabled for REST API
        http.csrf(csrf -> csrf.disable());

        // Session management - stateless
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Authentication entry point
        http.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()));

        // Configure authorization
        http.authorizeHttpRequests(auth -> auth
                // Public endpoints - documentation and health
                .requestMatchers(
                        "/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/actuator/health"
                ).permitAll()
                // Internal endpoints for service-to-service communication
                .requestMatchers("/internal/**").hasRole("SERVICE")
                // All other endpoints require authentication
                .anyRequest().authenticated()
        );

        // Add JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
} 