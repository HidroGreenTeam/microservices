package com.ayni.notification_service.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI learningPlatformOpenApi() {
        
        var openApi = new OpenAPI();
        openApi
                .info(new Info()
                        .title("HidroGreen Notification API")
                        .description("HidroGreen Notification Service REST API documentation.")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("HidroGreen Documentation")
                        .url("https://github.com/IoT-Solutions-SW71-Grupo-4/HidroBots-Report"));

        return openApi;
    }
} 