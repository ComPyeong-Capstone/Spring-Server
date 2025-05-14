package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.config.JwtTokenProvider;
import com.example.AIVideoApp.entity.User;
import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
                    JacksonFactory.getDefaultInstance())
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
                String jwt = jwtTokenProvider.createToken(user.getUserId().toString());

                Map<String, Object> response = new HashMap<>();
                response.put("token", jwt);
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

}
