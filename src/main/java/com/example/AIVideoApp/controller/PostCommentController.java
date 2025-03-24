package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.PostCommentDTO;
import com.example.AIVideoApp.service.CommentLikeService;
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

    /**
     * ✅ 댓글 좋아요 추가
     */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> likeComment(
            @PathVariable Integer commentId,
            @RequestParam Integer userId) {

        commentLikeService.likeComment(userId, commentId);
        return ResponseEntity.ok("댓글 좋아요 성공!");
    }

    /**
     * ✅ 댓글 좋아요 취소
     */
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<String> unlikeComment(
            @PathVariable Integer commentId,
            @RequestParam Integer userId) {

        commentLikeService.unlikeComment(userId, commentId);
        return ResponseEntity.ok("댓글 좋아요 취소 성공!");
    }

    /**
     * ✅ 댓글 좋아요 개수 조회
     */
    @GetMapping("/{commentId}/likes/count")
    public ResponseEntity<Long> getCommentLikeCount(@PathVariable Integer commentId) {
        long likeCount = commentLikeService.countLikes(commentId);
        return ResponseEntity.ok(likeCount);
    }
}
