package com.example.AIVideoApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "HashTag")
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer hashId;

    @Column(nullable = false, unique = true)
    private String hashName;
}

