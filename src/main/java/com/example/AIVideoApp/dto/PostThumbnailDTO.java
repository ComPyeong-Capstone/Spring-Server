package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter@Setter
@NoArgsConstructor
public class PostThumbnailDTO {
    private Integer postId;
    private String title;
    private LocalDateTime updateTime;
    private String thumbnailURL;
    private UserDTO author;

    public PostThumbnailDTO(Post post){
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.updateTime = post.getUpdateTime();
        this.thumbnailURL = post.getThumbnailURL();
        this.author = new UserDTO(post.getUser());
    }
}
