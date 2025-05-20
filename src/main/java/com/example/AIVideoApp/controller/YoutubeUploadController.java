package com.example.AIVideoApp.controller;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/youtube")
public class YoutubeUploadController {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "AI Video Uploader";
    private static final List<String> VALID_PRIVACY_STATUSES = List.of("public", "private", "unlisted");

    @PostMapping("/upload")
    public ResponseEntity<?> uploadToYouTube(@RequestBody Map<String, String> request) {
        String accessToken = request.get("YoutubeaccessToken");
        String videoUrl = request.get("videoUrl");
        String title = request.getOrDefault("title", "AI Generated Video");
        String description = request.getOrDefault("description", "Uploaded via Spring Server");
        String privacyStatus = request.getOrDefault("privacyStatus", "unlisted");

        if (accessToken == null || videoUrl == null) {
            return ResponseEntity.badRequest().body("accessToken 또는 videoUrl 누락됨");
        }

        if (!VALID_PRIVACY_STATUSES.contains(privacyStatus)) {
            return ResponseEntity.badRequest().body("올바르지 않은 privacyStatus 값입니다 (허용값: public, private, unlisted)");
        }

        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            HttpRequestInitializer requestInitializer = request1 ->
                    request1.getHeaders().setAuthorization("Bearer " + accessToken);

            YouTube youtube = new YouTube.Builder(httpTransport, JSON_FACTORY, requestInitializer)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // 영상 메타데이터 설정
            Video videoMetadata = new Video();
            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle(title);
            snippet.setDescription(description);

            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus(privacyStatus);

            videoMetadata.setSnippet(snippet);
            videoMetadata.setStatus(status);

            // 영상 스트림 준비 (S3에서 다운로드)
            URL url = new URL(videoUrl);
            InputStream inputStream = url.openStream();
            InputStreamContent mediaContent = new InputStreamContent("video/*", inputStream);

            YouTube.Videos.Insert uploadRequest = youtube.videos()
                    .insert("snippet,status", videoMetadata, mediaContent);
            Video uploadedVideo = uploadRequest.execute();

            String videoId = uploadedVideo.getId();
            String videoLink = "https://youtu.be/" + videoId;

            return ResponseEntity.ok(Map.of(
                    "videoLink", videoLink
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("업로드 실패: " + e.getMessage());
        }
    }
}
