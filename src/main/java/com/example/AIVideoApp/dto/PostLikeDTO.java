package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.PostLike;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
public class PostLikeDTO {
    private Integer likeId;
    private Integer userId;
    private Integer postId;

    public PostLikeDTO(PostLike like){
        this.likeId = like.getLikeId();
        this.userId = like.getUser().getUserId();
        this.postId = like.getPost().getPostId();


    }
}
