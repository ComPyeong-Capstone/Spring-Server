package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {
    private Integer senderId;
    private Integer receiverId;
    private Integer postId;
    private NotificationType type;
}
