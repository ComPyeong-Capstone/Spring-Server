package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.PostComment;
import lombok.*;

@Data
public class PostCommentDTO {
    private final Integer commentId;
    private final Integer userId;
    private final Integer postId;
    private final String content;

    public PostCommentDTO(PostComment comment){
        this.commentId = comment.getCommentId();
        this.userId = comment.getUser().getUserId();
        this.postId = comment.getPost().getPostId();
        this.content = comment.getContent();

    }
}
