package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.exception.EmailNotFoundException;
import com.example.AIVideoApp.exception.InvalidPasswordException;
import com.example.AIVideoApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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

            // 이후 JWT 토큰 발급 예정이라면 여기서 토큰 생성해서 반환
            return ResponseEntity.ok(userDTO);

        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // 프로필 이미지 설정
    @PutMapping("/profile-image")
    public ResponseEntity<String> updateProfileImage(@RequestParam Integer userId, @RequestBody Map<String, String> request) {
        try {
            userService.updateProfileImage(userId, request.get("profileImageUrl"));
            return ResponseEntity.ok("프로필 이미지 변경 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 닉네임 설정
    @PutMapping("/nickname")
    public ResponseEntity<String> updateNickname(@RequestParam Integer userId, @RequestBody Map<String, String> request) {
        try {
            userService.updateNickname(userId, request.get("newNickname"));
            return ResponseEntity.ok("닉네임 변경 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
