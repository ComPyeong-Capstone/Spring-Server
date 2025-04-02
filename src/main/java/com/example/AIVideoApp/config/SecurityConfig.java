package com.example.AIVideoApp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ 회원가입 & 로그인은 누구나 허용
                        .requestMatchers(HttpMethod.POST, "/users", "/users/login").permitAll()

                        // ✅ 게시물 전체 조회, 특정 해시태그로 조회, 좋아요 유저 조회는 허용
                        .requestMatchers(HttpMethod.GET, "/posts", "/posts/**/likes/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts", "/posts/**").permitAll()

                        // ✅ 댓글 조회만 허용 (추가, 삭제 등은 인증 필요)
                        .requestMatchers(HttpMethod.GET, "/posts/**/comments").permitAll()

                        // ✅ 알림 목록 조회 허용, 생성은 인증 필요
                        .requestMatchers(HttpMethod.GET, "/notifications").permitAll()

                        // ✅ 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
