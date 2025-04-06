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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/oauth")
public class OAuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
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
                // 🧩 회원가입 필요 응답
                Map<String, Object> response = new HashMap<>();
                response.put("needSignup", true);
                response.put("email", email); // 이메일 전달해서 나중에 재사용
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

        if (userRepository.findByUserName(nickname).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 닉네임입니다.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 이메일입니다.");
        }

        User user = new User();
        user.setEmail(email);
        user.setUserName(nickname);
        user.setPassword("SOCIAL_LOGIN_USER");
        userRepository.save(user);

        String jwt = jwtTokenProvider.createToken(user.getUserId().toString());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", new UserDTO(user));
        return ResponseEntity.ok(response);
    }

}
