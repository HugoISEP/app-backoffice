package com.mycompany.myapp.config;

import io.github.jhipster.config.apidoc.customizer.SwaggerCustomizer;
import springfox.documentation.service.*;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApplicationSwaggerCustomizer implements SwaggerCustomizer {



    @Override
    public void customize(Docket docket) {
        docket.securitySchemes(Collections.singletonList(apiKey()));
        docket.securityContexts(Collections.singletonList(securityContext()));
    }


    private ApiKey apiKey() {
        return new ApiKey("API Key", "x-api-key", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
}
