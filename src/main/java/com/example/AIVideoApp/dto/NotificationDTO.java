package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.Notification;
import com.example.AIVideoApp.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Integer notiId;
    private Integer senderId;    // 알림을 보낸 사용자
    private Integer receiverId;  // 알림을 받은 사용자
    private Integer postId;      // 관련 게시물 ID
    private NotificationType notiType;         // 알림 유형 ("like", "comment")
    private boolean notiRead;      // 읽음 여부
    private LocalDateTime notiTime; // 알림 생성 시간

    // **Notification 엔티티를 DTO로 변환하는 생성자**
    public NotificationDTO(Notification notification) {
        this.notiId = notification.getNotiId();
        this.senderId = notification.getSender().getUserId();
        this.receiverId = notification.getReceiver().getUserId();
        this.postId = notification.getPost().getPostId();
        this.notiType = notification.getNotiType();
        this.notiRead = notification.getNotiRead();
        this.notiTime = notification.getNotiTime();

    }
}
