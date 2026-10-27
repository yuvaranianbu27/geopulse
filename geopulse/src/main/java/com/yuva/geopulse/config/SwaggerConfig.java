package com.yuva.geopulse.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI geopulseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GeoPulse API")
                        .description("Driver location ingestion and nearby driver matching service for the GeoPulse platform.")
                        .version("v1")
                        .contact(new Contact()
                                .name("GeoPulse Team")
                                .email("support@geopulse.example"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
