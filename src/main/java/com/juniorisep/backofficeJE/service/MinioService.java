package com.juniorisep.backofficeJE.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class MinioService {

    @Autowired
    MinioClient minioClient;

    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteFile(String fileName, String bucket) throws MinioException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(fileName)
                .build());
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }

    public String getFileUrl(String fileName, String bucket) throws MinioException {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucket)
                .object(fileName)
                .method(Method.GET)
                .build());
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }
    }

    public void uploadFile(MultipartFile file, String fileName, String bucket) throws MinioException {
        try {
            if(minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())){
                minioClient.putObject(PutObjectArgs.builder()
                    .contentType(file.getContentType())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), 6000000)
                    .bucket(bucket)
                    .build());
            } else {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new MinioException(e.getMessage());
        }

    }

}
