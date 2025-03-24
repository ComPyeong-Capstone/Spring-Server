package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.PostComment;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class PostCommentDTO {
    private final Integer commentId;
    private final Integer userId;
    private final Integer postId;
    private final String content;
    private final Integer parentCommentID;
    private final LocalDateTime createdAt;
    private final int likeCount;
    private final boolean likedByMe;

    public PostCommentDTO(PostComment comment, Integer currentUserId){
        this.commentId = comment.getCommentId();
        this.userId = comment.getUser().getUserId();
        this.postId = comment.getPost().getPostId();
        this.content = comment.getContent();
        this.parentCommentID = comment.getParent() != null
                ? comment.getParent().getCommentId()
                : null;
        this.createdAt = comment.getCreatedAt();
        this.likeCount = comment.getLikes().size();
        this.likedByMe = comment.getLikes().stream()
                .anyMatch(like -> like.getUser().getUserId().equals(currentUserId));
    }
}
