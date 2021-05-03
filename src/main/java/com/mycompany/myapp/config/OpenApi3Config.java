package com.mycompany.myapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
class OpenApi3Config {

    @Value("${jhipster.swagger.title}")
    private String appName;

    @Value("${jhipster.swagger.description}")
    private String description;

    @Value("${jhipster.swagger.version}")
    private String version;

    @Value("${jhipster.swagger.terms-of-service-url}")
    private String termsOfServiceUrl;

    @Value("${jhipster.swagger.contact-name}")
    private String contactName;

    @Value("${jhipster.swagger.license}")
    private String license;

    @Value("${jhipster.swagger.license-url}")
    private String licenseUrl;




    @Bean
    public OpenAPI publicDocumentationConfig() {
        return new OpenAPI()
            .info(new Info().title(this.appName)
                .description(description)
                .version(version)
                .contact(new Contact().name(contactName))
                .license(new License().name(license).url(licenseUrl))
                .termsOfService(termsOfServiceUrl)
                .extensions(Collections.singletonMap("x-logo",
                    Map.of("url","https://upload.wikimedia.org/wikipedia/commons/7/7d/JE_logo.png",
                        "backgroundColor","#ffffff", "altText", "JE Consultants logo")))
            )
            .components(new Components()
                .addSecuritySchemes("apiKey", new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("x-api-key"))
                .addSecuritySchemes("oauth2", new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .bearerFormat("bearer")
                )
            );
    }
}
