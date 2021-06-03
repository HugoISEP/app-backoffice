package com.mycompany.myapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger3Config {
    @Bean
    public ApplicationSwaggerCustomizer applicationSwaggerCustomizer() {
        return new ApplicationSwaggerCustomizer();
    }
}
