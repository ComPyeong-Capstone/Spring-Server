package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.PostLikeDTO;
import com.example.AIVideoApp.service.PostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping
    public ResponseEntity<String> likePost(@PathVariable Integer postId, @RequestBody PostLikeDTO postLikeDTO) {

        String message = postLikeService.likePost(postLikeDTO);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> unlikePost(@PathVariable Integer postId, @PathVariable Integer userId) {
        String message = postLikeService.unlikePost(postId, userId);
        return ResponseEntity.ok(message);
    }

    @GetMapping
    public ResponseEntity<Long> getLikeCount(@PathVariable Integer postId) {
        long count = postLikeService.getLikeCount(postId);
        return ResponseEntity.ok(count);
    }
}
