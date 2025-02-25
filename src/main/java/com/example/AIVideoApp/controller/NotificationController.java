package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.NotificationDTO;
import com.example.AIVideoApp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> createNotification(
            @RequestParam Integer senderId,
            @RequestParam Integer receiverId,
            @RequestParam Integer postId,
            @RequestParam String type) {
        try {
            notificationService.createNotification(senderId, receiverId, postId, type);
            return ResponseEntity.ok("알림이 성공적으로 생성되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 2️⃣ **특정 사용자의 알림 목록 조회 (GET /notifications?userId={userId})**
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUser(@RequestParam Integer userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    // 3️⃣ **알림 읽음 처리 (PUT /notifications/{notiId}/read)**
    @PutMapping("/{notiId}/read")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Integer notiId) {
        try {
            notificationService.markNotificationAsRead(notiId);
            return ResponseEntity.ok("알림이 성공적으로 읽음 처리되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
