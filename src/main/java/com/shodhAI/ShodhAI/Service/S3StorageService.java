package com.shodhAI.ShodhAI.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3StorageService(@Value("${aws.access-key}") String accessKey,
                            @Value("${aws.secret-key}") String secretKey,
                            @Value("${aws.region}") String region) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        this.presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    // ✅ Upload File with Unique Key (UUID + Timestamp)
    public String uploadFile(File file, String originalFilename) {
        String uniqueKey = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + "_" + originalFilename;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueKey)
                .contentType("image/png") // Modify as needed
                .build();

        s3Client.putObject(request, Path.of(file.getAbsolutePath()));
        return uniqueKey; // ✅ Return the unique key instead of a public URL
    }

    // ✅ Generate Pre-Signed URL for Secure Access
    public URL getPresignedUrl(String key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60)) // URL expires in 60 mins
                .getObjectRequest(b -> b.bucket(bucketName).key(key))
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        return presignedRequest.url(); // ✅ This URL grants temporary access to the file
    }
}
