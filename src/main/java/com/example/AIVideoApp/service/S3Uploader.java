package com.example.AIVideoApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}") // ✅ bucket 이름 주입
    private String bucket;

    @Value("${cloud.aws.region.static}") // ✅ region 주입
    private String region;

    // ✅ [1] 기존 MultipartFile 업로드
    public String upload(MultipartFile file, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename(); // UUID 고유화

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return getFileUrl(fileName); // 저장된 파일 URL 반환

        } catch (S3Exception e) {
            throw new RuntimeException("S3 파일 업로드 실패: " + e.awsErrorDetails().errorMessage());
        }
    }

    // ✅ [2] byte[] 업로드용 오버로딩 메서드 (FastAPI 썸네일용)
    public String upload(byte[] bytes, String dirName, String extension) {
        String fileName = dirName + "/" + UUID.randomUUID() + "." + extension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType("image/" + extension)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(bytes)
            );

            return getFileUrl(fileName);

        } catch (S3Exception e) {
            throw new RuntimeException("S3 썸네일 업로드 실패: " + e.awsErrorDetails().errorMessage());
        }
    }

    private String getFileUrl(String fileName) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName; // ✅ region 직접 사용
    }
}
