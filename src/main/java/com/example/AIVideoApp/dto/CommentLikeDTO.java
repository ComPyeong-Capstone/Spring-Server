package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.CommentLike;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentLikeDTO {
    private Integer likeId;
    private Integer userId;
    private Integer commentId;

    public CommentLikeDTO(CommentLike commentLike) {
        this.likeId = commentLike.getLikeId();
        this.userId = commentLike.getUser().getUserId();
        this.commentId = commentLike.getComment().getCommentId();
    }
}
