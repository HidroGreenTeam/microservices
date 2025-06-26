package com.hidrogreen.treatment_service.shared.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
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

    @Value("${authorization.jwt.secret}")
    private String secret;

    @Value("${authorization.jwt.expiration.days}")
    private int expirationDays;

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

    /**
     * Validate a token (both user and service tokens)
     */
    public boolean isTokenValid(String token) {
        try {
            if (isServiceToken(token)) {
                Jwts.parser().verifyWith(getServiceSignInKey()).build().parseSignedClaims(token);
            } else {
                Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        try {
            Claims claims;
            if (isServiceToken(token)) {
                claims = Jwts.parser().verifyWith(getServiceSignInKey()).build()
                        .parseSignedClaims(token).getPayload();
            } else {
                claims = Jwts.parser().verifyWith(getSignInKey()).build()
                        .parseSignedClaims(token).getPayload();
            }
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if token is a service token
     */
    public boolean isServiceToken(String token) {
        try {
            // Try to parse with service key first
            Claims claims = Jwts.parser().verifyWith(getServiceSignInKey()).build()
                    .parseSignedClaims(token).getPayload();
            return "service".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private SecretKey getServiceSignInKey() {
        return Keys.hmacShaKeyFor(serviceSecret.getBytes());
    }
} 