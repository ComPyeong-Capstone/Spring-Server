package com.example.AIVideoApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    private Integer userId; // 유저 ID를 PK로 사용 (User와 1:1 관계)

    private String token;

    private LocalDateTime expiryDate;
}
