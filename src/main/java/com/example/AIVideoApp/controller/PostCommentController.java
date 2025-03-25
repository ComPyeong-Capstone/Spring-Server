package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.CommentRequestDTO;
import com.example.AIVideoApp.dto.PostCommentDTO;
import com.example.AIVideoApp.service.CommentLikeService;
import com.example.AIVideoApp.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<PostCommentDTO>> getComments(@PathVariable Integer postId, @RequestParam Integer currentUserId) {
        List<PostCommentDTO> comments = postCommentService.getCommentsByPostId(postId, currentUserId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 추가
    @PostMapping
    public ResponseEntity<PostCommentDTO> addComment(
            @PathVariable Integer postId,
            @RequestParam Integer userId,
            @RequestBody CommentRequestDTO commentRequest) {

        // 나중엔 여기서 userId = jwtProvider.getUserIdFromToken(request) 처럼 바꾸면 됨
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
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @RequestParam Integer userId) {
        try {
            postCommentService.deleteComment(postId, commentId, userId);
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 🔥 권한 문제일 경우 403 반환
        }
    }

    /**
     * ✅ 댓글 좋아요 추가
     */
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<String> likeComment(
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @RequestParam Integer userId) {

        commentLikeService.likeComment(userId, commentId);
        return ResponseEntity.ok("댓글 좋아요 성공!");
    }

    /**
     * ✅ 댓글 좋아요 취소
     */
    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<String> unlikeComment(
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @RequestParam Integer userId) {

        commentLikeService.unlikeComment(userId, commentId);
        return ResponseEntity.ok("댓글 좋아요 취소 성공!");
    }

}
