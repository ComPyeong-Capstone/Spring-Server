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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

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

            // ✅ 토큰과 유저 정보를 함께 반환
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userDTO);

            return ResponseEntity.ok(response);

        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // 프로필 이미지 설정
    @PutMapping("/profile-image")
    public ResponseEntity<String> updateProfileImage(
            @AuthenticationPrincipal Integer userId,
            @RequestBody Map<String, String> request
    ) {
        try {
            userService.updateProfileImage(userId, request.get("profileImageUrl"));
            return ResponseEntity.ok("프로필 이미지 변경 성공");
        } catch (RuntimeException e) {
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
