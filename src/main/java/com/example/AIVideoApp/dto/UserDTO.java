package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor    // @NonNull이 붙은 필드를 포함한 생성자를 자동으로 생성
public class UserDTO {
    private final Integer userId;
    private final String userName;
    private final String profileImage;

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.profileImage = user.getProfileImage();
    }
}
