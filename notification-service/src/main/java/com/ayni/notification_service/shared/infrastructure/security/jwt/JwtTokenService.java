package com.ayni.notification_service.shared.infrastructure.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtTokenService {

    @Value("${service.auth.secret}")
    private String serviceSecret;

    @Value("${service.auth.expiration.hours}")
    private int serviceExpirationHours;

    /**
     * Generate a service-to-service token for internal communication
     */
    public String generateServiceToken(String serviceName) {
        var issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        var expiration = issuedAt.plus(serviceExpirationHours, ChronoUnit.HOURS);
        
        return Jwts.builder()
                .subject(serviceName)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .claim("type", "service")
                .claim("service", serviceName)
                .signWith(getServiceSignInKey())
                .compact();
    }

    private SecretKey getServiceSignInKey() {
        return Keys.hmacShaKeyFor(serviceSecret.getBytes());
    }
} 