package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.PostCommentDTO;
import com.example.AIVideoApp.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;

    // 특정 게시물의 댓글 조회
    @GetMapping
    public ResponseEntity<List<PostCommentDTO>> getComments(@PathVariable Integer postId) {
        List<PostCommentDTO> comments = postCommentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 추가
    @PostMapping
    public ResponseEntity<PostCommentDTO> addComment(
            @PathVariable Integer postId,
            @RequestParam Integer userId,
            @RequestParam String content,
            @RequestParam(required = false) Integer parentCommentId) {

        PostCommentDTO commentDTO = postCommentService.addComment(postId, userId, content, parentCommentId);
        return ResponseEntity.ok(commentDTO);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}/users/{userId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @PathVariable Integer userId) {
        postCommentService.deleteComment(postId, commentId, userId);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
}
