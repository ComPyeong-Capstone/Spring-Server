package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter@Setter
@NoArgsConstructor
public class PostDTO {
    private Integer postId;
    private String title;
    private LocalDateTime updateTime;
    private String videoURL;
    private int likeCount;
    private int commentCount;
    private List<String> hashtags;
    private UserDTO author;

    // ✅ `Post` 엔티티를 받아서 `PostDTO`로 변환하는 생성자 추가
    public PostDTO(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.updateTime = post.getUpdateTime();
        this.videoURL = post.getVideoURL();
        this.likeCount = post.getPostLikes().size();
        this.commentCount = post.getPostComments().size();
        this.hashtags = post.getPostHashTags().stream()
                .map(postHashTag -> postHashTag.getHashTag().getHashName())
                .collect(Collectors.toList());
        this.author = new UserDTO(post.getUser());
    }
}
