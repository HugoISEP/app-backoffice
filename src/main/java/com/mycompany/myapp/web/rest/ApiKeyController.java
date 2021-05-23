package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ApiUser;
import com.mycompany.myapp.service.ApiKeyService;
import com.mycompany.myapp.web.rest.vm.LoginVM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/api-authenticate")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @GetMapping("/{apiKey}")
    public ResponseEntity<?> fetchUserByKey(@PathVariable String apiKey) {
        Optional<ApiUser> user = apiKeyService.fetchUserByKey(apiKey);  //fetch a user
        LoginVM login = apiKeyService.loginUser(user.get());            //login the corresponding user
        return ResponseEntity.ok(login);
    }
}
