package com.example.AIVideoApp.config;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    // ✅ 보안상 실제 운영에선 더 복잡한 키를 환경변수나 설정파일로 관리해야 함
    private final long expirationTime = 1000L * 60 * 60 * 24; // 24시간

    // ✅ 토큰 생성 - userId를 subject에 저장
    public String createToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    // ✅ 토큰에서 userId 추출 (String → Integer로 변환)
    public Integer getUserIdFromToken(String token) {
        try {
            String subject = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return Integer.parseInt(subject); // 💡 예외 대비 try-catch 가능
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }

    // ✅ 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}