package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.PostComment;
import lombok.*;

@Data
public class PostCommentDTO {
    private Integer commentId;
    private Integer userId;
    private Integer postId;
    private String content;

    public static PostCommentDTO of(PostComment postcomment){
        PostCommentDTO postcommentDTO = new PostCommentDTO();
        postcommentDTO.setCommentId(postcomment.getCommentId());
        return postcommentDTO;
    }

}
