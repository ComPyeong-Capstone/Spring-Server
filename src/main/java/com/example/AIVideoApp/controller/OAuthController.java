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
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/oauth")
public class OAuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;

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

        if (userRepository.findByUsername(nickname).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 닉네임입니다.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 이메일입니다.");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(nickname);
        user.setPassword("SOCIAL_LOGIN_USER");
        userRepository.save(user);

        String jwt = jwtTokenProvider.createToken(user.getUserId().toString());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", new UserDTO(user));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/kakao/token")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> request) {
        String code = request.get("code");

        // 1. 액세스 토큰 요청
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", kakaoRedirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenUrl, HttpMethod.POST, tokenRequest, Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 2. 사용자 정보 요청
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                userInfoRequest,
                Map.class
        );

        Map<String, Object> userInfo = userInfoResponse.getBody();
        Long kakaoId = ((Number) userInfo.get("id")).longValue();
        String email = "kakao_" + kakaoId + "@noemail.com";

        // 3. DB 확인 및 응답
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
    }

    @PostMapping("/kakao/signup")
    public ResponseEntity<?> kakaoSignup(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String nickname = request.get("nickname");

        if (userRepository.findByUsername(nickname).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 닉네임입니다.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 이메일입니다.");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(nickname);
        user.setPassword("SOCIAL_LOGIN_USER");
        userRepository.save(user);

        String jwt = jwtTokenProvider.createToken(user.getUserId().toString());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", new UserDTO(user));
        return ResponseEntity.ok(response);
    }

//    // 웹 테스트 용
//    @GetMapping("/kakao")
//    public ResponseEntity<?> kakaoRedirect(@RequestParam String code) {
//        // 클라이언트가 받은 인가 코드를 HTML에서 다시 JS로 처리할 수 있게 전달
//        String html = "<script>" +
//                "window.opener.postMessage({ code: '" + code + "' }, '*');" +
//                "window.close();" +
//                "</script>";
//        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
//    }

    // 웹뷰 반환을 위한 메소드
    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoRedirect(@RequestParam String code) {
        String html = "<script>" +
                "window.ReactNativeWebView.postMessage('" + code + "');" +
                "</script>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

}
