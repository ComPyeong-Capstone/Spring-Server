package com.example.AIVideoApp.repository;

import com.example.AIVideoApp.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query("SELECT n FROM Notification n JOIN FETCH n.sender JOIN FETCH n.post WHERE n.receiver.userId = :userId")
    List<Notification> findWithSenderByReceiverId(@Param("userId") Integer userId);

}
