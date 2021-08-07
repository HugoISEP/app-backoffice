package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.ApiUser;
import com.mycompany.myapp.repository.ApiKeyRepository;
import com.mycompany.myapp.web.rest.vm.LoginVM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public Optional<ApiUser> fetchUserByKey(String apiKey) {
        return apiKeyRepository.findOneByApiKey(apiKey);
    }

    public LoginVM loginUser(ApiUser apiUser) {
        LoginVM login = new LoginVM();
        login.setUsername(apiUser.getUser().getEmail());
        login.setPassword(apiUser.getUser().getPassword());
        login.setRememberMe(true);
        return login;
    }
}
