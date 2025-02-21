package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.PostCommentDTO;
import com.example.AIVideoApp.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<PostCommentDTO> createComment(@RequestBody PostCommentDTO postCommentDTO) {
        return ResponseEntity.ok(postCommentService.saveComment(postCommentDTO));
    }

    // 특정 게시글의 모든 댓글 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<PostCommentDTO>> getCommentsByPostId(@PathVariable Integer postId) {
        return ResponseEntity.ok(postCommentService.getCommentsByPostId(postId));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        postCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
