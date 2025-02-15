package com.example.AIVideoApp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Post_HashTag", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "hash_id"}))
public class PostHashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postHashId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "hash_id", nullable = false)
    private HashTag hashTag;
}

