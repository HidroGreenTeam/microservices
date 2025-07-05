package com.hidrogreen.payment.gateway.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI ayniPaymentServiceOpenApi(
            @Value("${documentation.application.description}") String appDescription,
            @Value("${documentation.application.version}") String appVersion) {

        final String securitySchemeName = "bearerAuth";
        
        var openApi = new OpenAPI();
        openApi
                .info(new Info()
                        .title("Ayni Payment Service API")
                        .description("Ayni Payment Service REST API documentation - Payment processing, PayPal integration, and transaction management.")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                        .contact(new Contact().name("Ayni Development Team")
                                .email("dev@ayni.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Ayni Documentation")
                        .url("https://github.com/Ayni-Team/Ayni-Documentation"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
        
        return openApi;
    }
}
