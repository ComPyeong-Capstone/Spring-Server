package com.example.AIVideoApp.service;

import com.example.AIVideoApp.dto.NotificationDTO;
import com.example.AIVideoApp.entity.Notification;
import com.example.AIVideoApp.entity.Post;
import com.example.AIVideoApp.entity.User;
import com.example.AIVideoApp.enums.NotificationType;
import com.example.AIVideoApp.repository.NotificationRepository;
import com.example.AIVideoApp.repository.PostRepository;
import com.example.AIVideoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 1️⃣ 알림 생성
    @Transactional
    public void createNotification(Integer senderId, Integer receiverId, Integer postId, NotificationType type) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("보낸 사용자를 찾을 수 없습니다."));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("받는 사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("해당 게시물을 찾을 수 없습니다."));

        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .post(post)
                .notiType(type)
                .build();

        notificationRepository.save(notification);
    }

    // 2️⃣ 특정 사용자의 모든 알림 목록 조회 (최신순)
    public List<NotificationDTO> getNotificationsByUser(Integer receiverId) {
        return notificationRepository.findWithSenderByReceiverId(receiverId)
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    // 3️⃣ 특정 알림을 읽음 처리
    @Transactional
    public void markNotificationAsRead(Integer notiId) {
        Notification notification = notificationRepository.findById(notiId)
                .orElseThrow(() -> new RuntimeException("해당 알림을 찾을 수 없습니다."));
        notification.setNotiRead(true);
        notificationRepository.save(notification);
    }

}
