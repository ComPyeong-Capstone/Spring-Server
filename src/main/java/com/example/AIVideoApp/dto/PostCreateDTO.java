package com.example.AIVideoApp.dto;

import com.example.AIVideoApp.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter@Setter
@NoArgsConstructor
public class PostCreateDTO {
    private Integer userId;
    private String title;
    private String thumbnailURL;
    private String videoURL;
    private List<String> hashtags;
    private List<String> imageUrls;
}
