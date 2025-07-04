
package com.hidrogreeen.payment.gateway.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI paymentServiceOpenApi(
            @Value("${documentation.application.description}") String appDescription,
            @Value("${documentation.application.version}") String appVersion) {

        var openApi = new OpenAPI();
        openApi
                .info(new Info()
                        .title("Payment Service API")
                        .description(appDescription)
                        .version(appVersion)
                        .license(new License().name("Apache 2.0").url("https://springdoc.org"))
                        .contact(new Contact().name("HidroGreen Team").email("contact@hidrogreen.com")))
                .servers(List.of(new Server().url("http://localhost:8086").description("Development Server")));
        return openApi;
    }
}
