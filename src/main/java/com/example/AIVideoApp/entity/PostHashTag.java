package com.example.AIVideoApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "post_hash_tag", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "hash_id"}))
public class PostHashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postHashId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hash_id", nullable = false)
    private HashTag hashTag;

    public PostHashTag(Post post, HashTag hashTag) {
        this.post = post;
        this.hashTag = hashTag;
    }
}
