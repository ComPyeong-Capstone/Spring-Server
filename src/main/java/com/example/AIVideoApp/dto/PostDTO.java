package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor // ✅ 모든 필드를 포함한 생성자 자동 생성
public class PostDTO {
    private Integer postId;
    private String title;
    private Integer userId;
    private LocalDateTime updateTime;
    private int likeCount;
    private int commentCount;
    private List<String> hashtags;

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
    }
}
