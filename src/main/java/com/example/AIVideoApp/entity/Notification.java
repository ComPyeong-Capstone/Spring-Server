package com.example.AIVideoApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notiId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String type; // 알림 유형 (예: "like", "comment")

    @Column(nullable = false)
    private Boolean isRead = false; // 읽음 여부 (기본값 false)

    @Column(nullable = false)
    private LocalDateTime notiTime = LocalDateTime.now(); // 생성 시간

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // 알림을 보낸 사용자

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // 알림을 받는 사용자
}
