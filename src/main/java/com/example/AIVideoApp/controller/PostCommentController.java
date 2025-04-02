package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.CommentRequestDTO;
import com.example.AIVideoApp.dto.PostCommentDTO;
import com.example.AIVideoApp.service.CommentLikeService;
import com.example.AIVideoApp.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;
    private final CommentLikeService commentLikeService;

    // 특정 게시물의 댓글 조회
    @GetMapping
    public ResponseEntity<List<PostCommentDTO>> getComments(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer userId // nullable
    ) {
        Integer currentUserId = null;
        if (userId != null) {
            currentUserId = userId;
        }

        List<PostCommentDTO> comments = postCommentService.getCommentsByPostId(postId, currentUserId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 추가
    @PostMapping
    public ResponseEntity<PostCommentDTO> addComment(
            @AuthenticationPrincipal Integer userId,
            @PathVariable Integer postId,
            @RequestBody CommentRequestDTO commentRequest
    ) {
        PostCommentDTO commentDTO = postCommentService.addComment(
                postId,
                userId,
                commentRequest.getContent(),
                commentRequest.getParentCommentId());

        return ResponseEntity.ok(commentDTO);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @AuthenticationPrincipal Integer userId,
            @PathVariable Integer postId,
            @PathVariable Integer commentId
    ) {
        try {
            postCommentService.deleteComment(postId, commentId, userId);
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * ✅ 댓글 좋아요 추가
     */
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<String> likeComment(
            @AuthenticationPrincipal Integer userId,
            @PathVariable Integer postId,
            @PathVariable Integer commentId
    ) {
        commentLikeService.likeComment(userId, commentId);
        return ResponseEntity.ok("댓글 좋아요 성공!");
    }

    /**
     * ✅ 댓글 좋아요 취소
     */
    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<String> unlikeComment(
            @AuthenticationPrincipal Integer userId,
            @PathVariable Integer postId,
            @PathVariable Integer commentId
    ) {
        commentLikeService.unlikeComment(userId, commentId);
        return ResponseEntity.ok("댓글 좋아요 취소 성공!");
    }

}
