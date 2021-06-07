package com.juniorisep.backofficeJE.config;

import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

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

    @Bean
    public MinioClient generateMinioClient() throws MinioException {
        try {
            MinioClient minio = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, accessSecret)
                .build();

            if(!minio.bucketExists(BucketExistsArgs.builder().bucket(Constants.LOGO_BUCKET).build())){
                minio.makeBucket(MakeBucketArgs.builder().bucket(Constants.LOGO_BUCKET).build());
            }
            minio.setBucketPolicy(SetBucketPolicyArgs.builder()
                .config(ResourceReader.asString(config))
                .bucket(Constants.LOGO_BUCKET)
                .build());
            return minio;
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }

}
