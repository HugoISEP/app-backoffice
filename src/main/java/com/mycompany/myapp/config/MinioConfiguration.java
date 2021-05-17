package com.mycompany.myapp.config;

import io.github.jhipster.config.JHipsterConstants;
import io.minio.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;


import static com.mycompany.myapp.config.Constants.LOGO_BUCKET;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MinioConfiguration {
    @Value("${spring.minio.accessKey}")
    String accessKey;
    @Value("${spring.minio.secretKey}")
    String accessSecret;
    @Value("${spring.minio.url}")
    String minioUrl;
    @Value("classpath:minio/policy-config.json")
    Resource config;

    private final Environment env;


    @Bean
    public MinioClient generateMinioClientNotProd() throws MinioException {
        try {
            MinioClient minio = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, accessSecret)
                .build();

            if(!minio.bucketExists(BucketExistsArgs.builder().bucket(LOGO_BUCKET).build())){
                minio.makeBucket(MakeBucketArgs.builder().bucket(LOGO_BUCKET).build());
            }
            minio.setBucketPolicy(SetBucketPolicyArgs.builder()
                .config(ResourceReader.asString(config))
                .bucket(LOGO_BUCKET)
                .build());
            return minio;

        } catch (Exception e) {
            if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_PRODUCTION))) {
                throw new MinioException(e.getMessage());
            }
            log.error("Failed to start minio");
            log.debug(e.getMessage());
            return null;
        }
    }

}
