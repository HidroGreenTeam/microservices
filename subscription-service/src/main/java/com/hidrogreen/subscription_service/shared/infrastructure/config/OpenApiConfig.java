package com.hidrogreen.subscription_service.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ayniSubscriptionServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ayni Subscription Service API")
                        .version("v1.0.0")
                        .description("Ayni Subscription Service REST API documentation - Subscription plans, billing, and payment management.")
                        .contact(new Contact()
                                .name("Ayni Development Team")
                                .email("dev@ayni.com")
                                .url("https://ayni.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("Ayni Documentation")
                        .url("https://github.com/Ayni-Team/Ayni-Documentation"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"));
    }
} 