package com.example.AIVideoApp.repository;

import com.example.AIVideoApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email); // 로그인 시 이메일 검색
}
