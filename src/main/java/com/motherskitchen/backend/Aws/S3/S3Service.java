package com.motherskitchen.backend.Aws.S3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    private final String bucketName = "mothers-kitchen-image-bucket";

    public UploadDTO uploadFile(MultipartFile file) throws IOException {

        String key = System.currentTimeMillis() + "_" + file.getOriginalFilename() + "_MothersKitchen";

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            String URL="https://" + bucketName + ".s3.amazonaws.com/" + key;
            return UploadDTO.builder()
                    .url(URL)
                    .key(key)
            .build();

        } catch (SdkClientException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }


    }

    public void deleteFile(String key) {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
        } catch (SdkClientException e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }
}
