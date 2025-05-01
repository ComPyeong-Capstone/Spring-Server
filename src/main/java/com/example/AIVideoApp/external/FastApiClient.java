package com.example.AIVideoApp.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FastApiClient {

    private final RestTemplate restTemplate;

    @Value("${fastapi.url}")
    private String fastApiUrl;

    public byte[] requestThumbnail(MultipartFile videoFile) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("video", new MultipartInputStreamFileResource(videoFile.getInputStream(), videoFile.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // ✅ 여기를 실제 fastapi.url 값과 연결
        ResponseEntity<byte[]> response = restTemplate.exchange(
                fastApiUrl + "/generate/thumbnail",
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("FastAPI 썸네일 생성 실패");
        }

        return response.getBody();
    }
}
