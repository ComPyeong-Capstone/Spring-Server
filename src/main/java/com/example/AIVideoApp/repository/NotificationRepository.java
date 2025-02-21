package com.example.AIVideoApp.repository;

import com.example.AIVideoApp.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // 1️⃣ 특정 사용자의 모든 알림 조회 (최신순)
    List<Notification> findByReceiver_UserIdOrderByNotiTimeDesc(Integer receiverId);

}
