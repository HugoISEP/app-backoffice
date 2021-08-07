package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ApiUser;
import com.mycompany.myapp.security.apiKey.KeyFilter;
import com.mycompany.myapp.service.ApiKeyService;
import com.mycompany.myapp.web.rest.vm.LoginVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @GetMapping("/apiKey-authenticate")
    @Transactional
    public ResponseEntity<?> authorize(@RequestHeader(KeyFilter.APIKEY_HEADER) String apiKey) {
        Optional<ApiUser> apiUser = fetchUserByKey(apiKey);
        if (apiUser.isPresent()){
            setContextAuthentification(apiUser.get());
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(KeyFilter.APIKEY_HEADER, "Bearer " + apiKey);
            return new ResponseEntity<>(null, httpHeaders, HttpStatus.OK);
        }
        return (ResponseEntity<?>) ResponseEntity.unprocessableEntity();
    }

    public Optional<ApiUser> fetchUserByKey(String apiKey){
        return apiKeyService.fetchUserByKey(apiKey);
    }

    public void setContextAuthentification (ApiUser apiUser) {
        LoginVM loginVM = apiKeyService.loginUser(apiUser);
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
