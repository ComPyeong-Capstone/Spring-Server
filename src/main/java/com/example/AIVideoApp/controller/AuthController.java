package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.config.JwtTokenProvider;
import com.example.AIVideoApp.entity.RefreshToken;
import com.example.AIVideoApp.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 리프레시 토큰입니다.");
        }

        Integer userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        RefreshToken saved = refreshTokenRepository.findById(userId)
                .orElse(null);

        if (saved == null || !saved.getToken().equals(refreshToken)) {
            return ResponseEntity.status(401).body("리프레시 토큰이 존재하지 않거나 일치하지 않습니다.");
        }

        if (saved.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(401).body("리프레시 토큰이 만료되었습니다.");
        }

        String newAccessToken = jwtTokenProvider.createToken(userId.toString());

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal Integer userId) {
        refreshTokenRepository.deleteById(userId);
        return ResponseEntity.ok("로그아웃되었습니다.");
    }

}
