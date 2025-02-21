package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.NotificationDTO;
import com.example.AIVideoApp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 1️⃣ **알림 생성 (POST /notifications)**
    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(
            @RequestParam Integer senderId,
            @RequestParam Integer receiverId,
            @RequestParam Integer postId,
            @RequestParam String type) {
        return ResponseEntity.ok(notificationService.createNotification(senderId, receiverId, postId, type));
    }

    // 2️⃣ **특정 사용자의 알림 목록 조회 (GET /notifications?userId={userId})**
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUser(@RequestParam Integer userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    // 3️⃣ **알림 읽음 처리 (PUT /notifications/{notiId}/read)**
    @PutMapping("/{notiId}/read")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(@PathVariable Integer notiId) {
        return ResponseEntity.ok(notificationService.markNotificationAsRead(notiId));
    }
}
