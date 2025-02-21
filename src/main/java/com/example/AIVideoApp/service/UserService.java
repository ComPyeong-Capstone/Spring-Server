package com.example.AIVideoApp.service;

import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.entity.User;
import com.example.AIVideoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 🔹 회원가입
    @Transactional
    public UserDTO registerUser(String userName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = new User(userName, email, passwordEncoder.encode(password), null);
        user = userRepository.save(user);

        return new UserDTO(user); // ✅ DTO 변환
    }

    // 🔹 로그인 (이메일과 비밀번호 검증)
    public UserDTO loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return new UserDTO(user); // ✅ DTO 변환
    }

    // 🔹 프로필 이미지 설정
    @Transactional
    public UserDTO updateProfileImage(Integer userId, String profileImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setProfileImage(profileImageUrl);
        userRepository.save(user);

        return new UserDTO(user); // ✅ DTO 변환
    }

    // 🔹 닉네임 변경
    @Transactional
    public UserDTO updateNickname(Integer userId, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setUserName(newNickname);
        userRepository.save(user);

        return new UserDTO(user); // ✅ DTO 변환
    }
}
