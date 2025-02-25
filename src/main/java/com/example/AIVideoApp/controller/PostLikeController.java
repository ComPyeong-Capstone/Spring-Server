package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.PostLikeDTO;
import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.service.PostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> likePost(@PathVariable Integer postId, @PathVariable Integer userId) {

        String message = postLikeService.likePost(postId, userId);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> unlikePost(@PathVariable Integer postId, @PathVariable Integer userId) {
        String message = postLikeService.unlikePost(postId, userId);
        return ResponseEntity.ok(message);
    }

    //좋아요를 누른 유저 수만 조회
    /*@GetMapping
    public ResponseEntity<Long> getLikeCount(@PathVariable Integer postId) {
        long count = postLikeService.getLikeCount(postId);
        return ResponseEntity.ok(count);
    }*/

    //좋아요를 누른 유저들 정보 조회
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getLikers(@PathVariable Integer postId) {
        List<UserDTO> likers = postLikeService.getLikers(postId);
        return ResponseEntity.ok(likers);
    }

}
