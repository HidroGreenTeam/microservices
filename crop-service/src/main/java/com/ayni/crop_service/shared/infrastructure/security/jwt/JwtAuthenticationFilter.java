package com.ayni.crop_service.shared.infrastructure.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String contentType = request.getContentType();
        
        logger.debug("Processing request: {} {} with Content-Type: {}", method, requestURI, contentType);
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No Authorization header or invalid format for: {} {}", method, requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        logger.debug("JWT token found for: {} {}", method, requestURI);
        
        try {
            if (jwtTokenService.isTokenValid(jwt)) {
                String username = jwtTokenService.extractUsername(jwt);
                logger.debug("Valid JWT token for user: {} on {} {}", username, method, requestURI);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Create authentication token
                    List<SimpleGrantedAuthority> authorities;
                    
                    if (jwtTokenService.isServiceToken(jwt)) {
                        // Service token gets service authority
                        authorities = List.of(new SimpleGrantedAuthority("ROLE_SERVICE"));
                        logger.debug("Service token authenticated for: {} {}", method, requestURI);
                    } else {
                        // User token gets user authority
                        authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                        logger.debug("User token authenticated for: {} {}", method, requestURI);
                    }
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } else {
                logger.debug("Invalid JWT token for: {} {}", method, requestURI);
            }
        } catch (Exception e) {
            logger.error("JWT validation failed for {} {}: {}", method, requestURI, e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
} 