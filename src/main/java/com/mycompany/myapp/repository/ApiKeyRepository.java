package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ApiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiUser, String> {

    Optional<ApiUser> findOneByApiKey(String apiKey);
}
