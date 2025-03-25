package com.example.AIVideoApp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    private String content;
    private Integer parentCommentId;
}