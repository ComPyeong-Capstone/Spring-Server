package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.User;
import lombok.*;

@Data
public class UserDTO {
    private Integer userId;
    private String userName;
    private String profileImage;
    private String email;

    public static UserDTO of(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getUserName());
        userDTO.setProfileImage(user.getProfileImage());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }
}

