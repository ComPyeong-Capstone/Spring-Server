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
    public ResponseEntity<UserDTO> registerUser(@RequestBody Map<String, String> request) {
        String userName = request.get("userName");
        String email = request.get("email");
        String password = request.get("password");
        return ResponseEntity.ok(userService.registerUser(userName, email, password));
    }

    // 로그인
    @GetMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestParam String email, @RequestParam String password) {
        return ResponseEntity.ok(userService.loginUser(email, password));
    }

    // 프로필 이미지 설정
    @PutMapping("/{userId}/profile-image")
    public ResponseEntity<UserDTO> updateProfileImage(@PathVariable Integer userId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.updateProfileImage(userId, request.get("profileImageUrl")));
    }

    // 닉네임 설정
    @PutMapping("/{userId}/nickname")
    public ResponseEntity<UserDTO> updateNickname(@PathVariable Integer userId, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(userService.updateNickname(userId, request.get("newNickname")));
    }
}
