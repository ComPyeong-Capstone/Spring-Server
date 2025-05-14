package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.config.JwtTokenProvider;
import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.exception.EmailNotFoundException;
import com.example.AIVideoApp.exception.InvalidPasswordException;
import com.example.AIVideoApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.AIVideoApp.repository.RefreshTokenRepository;
import com.example.AIVideoApp.entity.RefreshToken;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> request) {
        String userName = request.get("userName");
        String email = request.get("email");
        String password = request.get("password");

        try {
            userService.registerUser(userName, email, password);
            return ResponseEntity.ok("회원가입 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 실패 메시지 반환
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            if (email == null || password == null) {
                return ResponseEntity.badRequest().body("이메일과 비밀번호를 입력해야 합니다.");
            }

            UserDTO userDTO = userService.loginUser(email, password);

            // ✅ JwtTokenProvider를 사용해 토큰 생성
            String token = jwtTokenProvider.createToken(userDTO.getUserId().toString());
            String refreshToken = jwtTokenProvider.createRefreshToken(userDTO.getUserId().toString());

            // Refresh Token 저장
            RefreshToken refreshTokenEntity = new RefreshToken(
                    userDTO.getUserId(),
                    refreshToken,
                    LocalDateTime.now().plusDays(7)
            );
            refreshTokenRepository.save(refreshTokenEntity);

            // ✅ 토큰과 유저 정보를 함께 반환
            Map<String, Object> response = new HashMap<>();
            response.put("accesstoken", token);
            response.put("refreshToken", refreshToken);
            response.put("user", userDTO);

            return ResponseEntity.ok(response);

        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    // ✅ 수정된 부분: 파일 업로드로 프로필 이미지 설정하는 새로운 API 추가
    @PutMapping("/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @AuthenticationPrincipal Integer userId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String imageUrl = userService.uploadProfileImage(userId, file);
            return ResponseEntity.ok(imageUrl); // 업로드된 이미지 URL 반환
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    // 닉네임 설정
    @PutMapping("/nickname")
    public ResponseEntity<String> updateNickname(
            @AuthenticationPrincipal Integer userId,
            @RequestBody Map<String, String> request
    ) {
        try {
            userService.updateNickname(userId, request.get("newNickname"));
            return ResponseEntity.ok("닉네임 변경 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
