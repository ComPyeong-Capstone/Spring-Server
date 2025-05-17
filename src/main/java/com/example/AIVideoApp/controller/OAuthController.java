package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.config.JwtTokenProvider;
import com.example.AIVideoApp.entity.RefreshToken;
import com.example.AIVideoApp.entity.User;
import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.repository.RefreshTokenRepository;
import com.example.AIVideoApp.repository.UserRepository;
import com.example.AIVideoApp.service.S3Uploader;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository; // ✅ 추가
    private final S3Uploader s3Uploader;

    @Value("${oauth.google.ios-client-id}")
    private String googleIosClientId;

    @Value("${oauth.google.android-client-id}")
    private String googleAndroidClientId;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");
        String platform = request.get("platform"); // "ios" 또는 "android"

        if (idToken == null || platform == null) {
            return ResponseEntity.badRequest().body("idToken 또는 platform 누락됨");
        }

        try {
            String expectedClientId = platform.equalsIgnoreCase("ios") ? googleIosClientId : googleAndroidClientId;

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(expectedClientId))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Google ID 토큰입니다.");
            }

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();

            Optional<User> optionalUser = userRepository.findByEmail(email);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String accessToken = jwtTokenProvider.createToken(user.getUserId().toString());
                String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId().toString());

                RefreshToken refreshTokenEntity = new RefreshToken(
                        user.getUserId(),
                        refreshToken,
                        LocalDateTime.now().plusDays(7)
                );
                refreshTokenRepository.save(refreshTokenEntity);

                Map<String, Object> response = new HashMap<>();
                response.put("accessToken", accessToken);
                response.put("refreshToken", refreshToken);
                response.put("user", new UserDTO(user));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("needSignup", true);
                response.put("email", email);
                return ResponseEntity.ok(response);
            }

        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google 인증 오류: " + e.getMessage());
        }
    }

    @PostMapping("/google/signup")
    public ResponseEntity<?> googleSignup(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String nickname = request.get("nickname");

        // 닉네임 중복 확인
        if (userRepository.findByUsername(nickname).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 닉네임입니다.");
        }

        // 이메일 중복 확인
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 이메일입니다.");
        }

        String profileImageUrl = s3Uploader.getFileUrl("user-profiles/basic.jpeg");

        // 신규 유저 생성
        User user = new User();
        user.setEmail(email);
        user.setUsername(nickname);
        user.setPassword("SOCIAL_LOGIN_USER");
        user.setProfileImage(profileImageUrl);
        userRepository.save(user); // 먼저 저장

        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId().toString());
        RefreshToken refreshTokenEntity = new RefreshToken(
                user.getUserId(),
                refreshToken,
                LocalDateTime.now().plusDays(7)
        );
        refreshTokenRepository.save(refreshTokenEntity);

        String accessToken = jwtTokenProvider.createToken(user.getUserId().toString());

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("user", new UserDTO(user));
        return ResponseEntity.ok(response);
    }
}
