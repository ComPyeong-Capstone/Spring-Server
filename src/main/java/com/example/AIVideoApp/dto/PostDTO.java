package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostDTO {
    private final Integer postId;
    private final String title;
    private final Integer userId;
    private final LocalDateTime updateTime;
    private final int likeCount;
    private final int commentCount;
    private final List<String> hashtags;
    private final String videoUrl;

    // ✅ `Post` 엔티티를 받아서 `PostDTO`로 변환하는 생성자 추가
    public PostDTO(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.userId = post.getUser().getUserId();
        this.updateTime = post.getUpdateTime();
        this.likeCount = post.getPostLikes().size();
        this.commentCount = post.getPostComments().size();
        this.hashtags = post.getPostHashTags().stream()
                .map(postHashTag -> postHashTag.getHashTag().getHashName())
                .collect(Collectors.toList());
        this.videoUrl = (post.getVideo() != null) ? post.getVideo().getVideoUrl() : null;
    }
}
