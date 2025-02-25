package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.service.UserService;
import lombok.RequiredArgsConstructor;
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
    @GetMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestParam String email, @RequestParam String password) {
        return ResponseEntity.ok(userService.loginUser(email, password));
    }

    // 프로필 이미지 설정
    @PutMapping("/{userId}/profile-image")
    public ResponseEntity<String> updateProfileImage(@PathVariable Integer userId, @RequestBody Map<String, String> request) {
        try {
            userService.updateProfileImage(userId, request.get("profileImageUrl"));
            return ResponseEntity.ok("프로필 이미지 변경 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 닉네임 설정
    @PutMapping("/{userId}/nickname")
    public ResponseEntity<String> updateNickname(@PathVariable Integer userId, @RequestBody Map<String, String> request) {
        try {
            userService.updateNickname(userId, request.get("newNickname"));
            return ResponseEntity.ok("닉네임 변경 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
