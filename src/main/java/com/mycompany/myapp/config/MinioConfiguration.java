package com.mycompany.myapp.config;

import io.minio.*;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.mycompany.myapp.config.Constants.LOGO_BUCKET;

@Configuration
public class MinioConfiguration {
    @Value("${spring.minio.accessKey}")
    String accessKey;
    @Value("${spring.minio.secretKey}")
    String accessSecret;
    @Value("${spring.minio.url}")
    String minioUrl;
    @Value("classpath:minio/policy-config.json")
    Resource config;

    Logger logger = LoggerFactory.getLogger(MinioConfiguration.class);

    @Bean
    @Profile("!prod")
    public MinioClient generateMinioClientNotProd() throws MinioException {
        try {
            return getMinioClient();
        } catch (Exception e) {
            logger.error("Failed to start minio");
            logger.debug(e.getMessage());
            return null;
        }
    }

    @Bean
    @Profile("prod")
    public MinioClient generateMinioClientProd() throws MinioException {
        try {
            return getMinioClient();
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }



    public MinioClient getMinioClient() throws IOException, NoSuchAlgorithmException, InvalidKeyException, MinioException {
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
    }

}
