package com.ayni.notification_service.shared.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class ServiceAuthorizationFilter extends OncePerRequestFilter {

    private final ServiceTokenConfig serviceTokenConfig;

    public ServiceAuthorizationFilter(ServiceTokenConfig serviceTokenConfig) {
        this.serviceTokenConfig = serviceTokenConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        
        String internalToken = request.getHeader("X-Internal-Service-Token");
        if (internalToken != null && serviceTokenConfig.isValidInternalToken(internalToken)) {
            
            filterChain.doFilter(request, response);
            return;
        }
        
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            
            filterChain.doFilter(request, response);
            return;
        }
        
        
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("Unauthorized: Valid token required");
    }

    private boolean isPublicEndpoint(String path) {
        return path.contains("/swagger-ui") || 
               path.contains("/v3/api-docs") || 
               path.contains("/actuator/health") ||
               path.equals("/api/v1/health");
    }
} 