package com.ludonexus.playersphere.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI playerSphereOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("PlayerSphere API")
                .description("API for managing player profiles in LudoNexus")
                .version("1.0")
                .contact(new Contact()
                    .name("LudoNexus Team")
                    .email("contact@ludonexus.com")));
    }
}