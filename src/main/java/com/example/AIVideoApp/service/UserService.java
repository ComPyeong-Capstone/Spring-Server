package com.example.AIVideoApp.service;

import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.entity.User;
import com.example.AIVideoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.AIVideoApp.exception.EmailNotFoundException;
import com.example.AIVideoApp.exception.InvalidPasswordException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    // 🔹 회원가입
    @Transactional
    public void registerUser(String userName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.findByUserName(userName).isPresent()) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }
        User user = new User(userName, email, passwordEncoder.encode(password), null);
        userRepository.save(user); // 🔥 DTO 반환 없이 저장만 수행
    }

    // 🔹 로그인 (이메일과 비밀번호 검증)
    public UserDTO loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("이메일이 존재하지 않습니다.")); // ✅ 사용자 정의 예외로 변경

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다."); // ✅ 사용자 정의 예외로 변경
        }
        return new UserDTO(user); // ✅ 로그인 성공 시 UserDTO 반환
    }


    // 🔹 프로필 이미지 설정
    @Transactional
    public void updateProfileImage(Integer userId, String profileImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setProfileImage(profileImageUrl);
        userRepository.save(user); // ✅ 저장만 수행
    }

    // ✅ 수정된 부분: 파일 업로드 + 프로필 설정 메서드 추가
    @Transactional
    public String uploadProfileImage(Integer userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String s3Url = s3Uploader.upload(file, "user-profiles"); // user-profiles 폴더에 저장
        user.setProfileImage(s3Url);
        userRepository.save(user);

        return s3Url;
    }


    // 🔹 닉네임 변경
    @Transactional
    public void updateNickname(Integer userId, String newNickname) {
        if (userRepository.findByUserName(newNickname).isPresent()) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setUserName(newNickname);
        userRepository.save(user); // ✅ 저장만 수행
    }

}
