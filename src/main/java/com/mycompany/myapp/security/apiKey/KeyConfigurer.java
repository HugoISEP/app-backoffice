package com.mycompany.myapp.security.apiKey;

import com.mycompany.myapp.web.rest.ApiKeyController;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class KeyConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ApiKeyController keyController;

    public KeyConfigurer(ApiKeyController keyController) {
        this.keyController = keyController;
    }

    @Override
    public void configure(HttpSecurity http) {
        KeyFilter customFilter = new KeyFilter(keyController);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
