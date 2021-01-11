package com.mycompany.myapp.config;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {
    @Value("${spring.minio.accessKey}")
    String accessKey;
    @Value("${spring.minio.secretKey}")
    String accessSecret;
    @Value("${spring.minio.url}")
    String minioUrl;

    @Bean
    public MinioClient generateMinioClient() throws MinioException {
        try {
            return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, accessSecret)
                .build();
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }


}
