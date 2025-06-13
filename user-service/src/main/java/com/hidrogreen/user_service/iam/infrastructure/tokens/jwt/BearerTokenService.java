package com.hidrogreen.user_service.iam.infrastructure.tokens.jwt;


import jakarta.servlet.http.HttpServletRequest;
import com.hidrogreen.user_service.iam.application.internal.outboundservices.tokens.TokenService;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;

public interface BearerTokenService extends TokenService {

    String getBearerTokenFrom(HttpServletRequest request);
    String generateToken(Authentication authentication);

}
