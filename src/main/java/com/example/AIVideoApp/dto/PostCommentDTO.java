package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.PostComment;
import lombok.*;

@Data
public class PostCommentDTO {
    private Integer commentId;
    private Integer userId;
    private Integer postId;
    private String content;

    public PostCommentDTO(PostComment comment){
        this.commentId = comment.getCommentId();
        this.userId = comment.getUser().getUserId();
        this.postId = comment.getPost().getPostId();
        this.content = comment.getContent();

    }
}
