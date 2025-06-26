package com.hidrogreen.user_service.iam.infrastructure.tokens.jwt.services;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.time.DateUtils;
import com.hidrogreen.user_service.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class TokenServiceImpl implements BearerTokenService {

    private final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);
    private static final String AUTHORIZATION_PARAMETER_NAME = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final int TOKEN_BEGIN_INDEX = 7;

    @Value("${authorization.jwt.secret}")
    private String secret;

    @Value("${authorization.jwt.expiration.days}")
    private int expirationDays;

    @Value("${service.auth.secret}")
    private String serviceSecret;

    @Value("${service.auth.expiration.hours}")
    private int serviceExpirationHours;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private SecretKey getServiceSignInKey() {
        return Keys.hmacShaKeyFor(serviceSecret.getBytes());
    }

    /**
     * Generate a service-to-service token for internal communication
     */
    public String generateServiceToken(String serviceName) {
        var issuedAt = new Date();
        var expiration = DateUtils.addHours(issuedAt, serviceExpirationHours);
        
        return Jwts.builder()
                .subject(serviceName)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .claim("type", "service")
                .claim("service", serviceName)
                .signWith(getServiceSignInKey())
                .compact();
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

    private String buildTokenWithDefaultParameters(String username) {
        var issuedAt = new Date();
        var expiration = DateUtils.addDays(issuedAt, expirationDays);
        var key = getSigningKey();

        return Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .compact();
        
    }

    @Override
    public boolean validateToken(String token) {
        try {
            if (isServiceToken(token)) {
                Jwts.parser().verifyWith(getServiceSignInKey()).build().parseSignedClaims(token);
            } else {
                Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            }
            LOGGER.info("Token is valid");
            return true;

        } catch (SignatureException e) {
            LOGGER.error("Invalid Json Web Token signature: {}", e.getMessage());
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            LOGGER.error("Invalid Json Web Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("Expired Json Web Token: {}", e.getMessage());
        }
        return false;
    }


    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        String parameter = getAuthorizationParameterFrom(request);
        if (isTokenPresentIn(parameter) && isBearerTokenIn(parameter)) {
            return extractTokenFrom(parameter);
        }
        return null;    }

    @Override
    public String generateToken(Neo4jProperties.Authentication authentication) {
        return buildTokenWithDefaultParameters(authentication.getUsername());
    }


    @Override
    public String generateToken(String username) {
        return buildTokenWithDefaultParameters(username);
    }

    private Claims extractAllClaims(String token) {
        if (isServiceToken(token)) {
            return Jwts.parser()
                    .verifyWith(getServiceSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } else {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
    }

    private boolean isTokenPresentIn(String authorizationParameter) {
        return StringUtils.hasText(authorizationParameter);
    }

    private boolean isBearerTokenIn(String authorizationParameter) {
        return authorizationParameter.startsWith(BEARER_TOKEN_PREFIX);
    }

    private String extractTokenFrom(String authorizationHeaderParameter) {
        return authorizationHeaderParameter.substring(TOKEN_BEGIN_INDEX);
    }

    private String getAuthorizationParameterFrom(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_PARAMETER_NAME);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

}
