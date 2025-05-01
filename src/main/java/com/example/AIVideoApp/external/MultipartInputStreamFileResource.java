package com.example.AIVideoApp.external;

import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

public class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() {
        return -1; // 파일 길이를 정확히 알 수 없을 때 -1을 반환
    }
}