package com.mycompany.myapp.config;

import io.minio.MinioClient;
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
    public MinioClient generateMinioClient() {
        try {
            System.out.println("BEFORE");
            MinioClient client = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, accessSecret)
                .build();
            System.out.println("AFTER");
            return client;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
