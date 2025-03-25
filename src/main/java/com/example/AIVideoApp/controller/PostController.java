package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.PostDTO;
import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.service.PostLikeService;
import com.example.AIVideoApp.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostDTO post) {
        System.out.println("createPost() Called");
        try {
            postService.createPost(post); // ✅ 게시물 저장
            return ResponseEntity.ok("게시물이 성공적으로 등록되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // ✅ 예외 메시지 반환
        }
    }

    // 2️⃣ 전체 게시물 조회 (GET /posts)
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts()); // ✅ Post → PostDTO 반환
    }

    // 3️⃣ 특정 사용자의 게시물 조회 (GET /posts?userId={userId})
    @GetMapping(params = "userId")
    public ResponseEntity<List<PostDTO>> getPostsByUser(@RequestParam Integer userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId)); // ✅ Post → PostDTO 반환
    }

    // 4️⃣ 특정 해시태그를 가진 게시물 조회 (GET /posts?hashtag={hashtag})
    @GetMapping(params = "hashtag")
    public ResponseEntity<List<PostDTO>> getPostsByHashTag(@RequestParam String hashtag) {
        return ResponseEntity.ok(postService.getPostsByHashTag(hashtag)); // ✅ Post → PostDTO 반환
    }

    // 5️⃣ 게시물 삭제 (DELETE /posts/{postId})
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Integer postId) {
        try {
            postService.deletePost(postId);
            return ResponseEntity.ok("게시물이 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 6️⃣ 게시물 수정 (PUT /posts/{postId})
    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Integer postId, @RequestBody PostDTO updatedPost) {
        Optional<PostDTO> updated = postService.updatePost(postId, updatedPost); // ✅ Post → PostDTO 반환
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 게시글 좋아요 추가
    @PostMapping("/{postId}/likes")
    public ResponseEntity<String> likePost(@PathVariable Integer postId, @RequestParam Integer userId) {
        String message = postLikeService.likePost(postId, userId);
        return ResponseEntity.ok(message);
    }

    // 게시글 좋아요 취소
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<String> unlikePost(@PathVariable Integer postId, @RequestParam Integer userId) {
        String message = postLikeService.unlikePost(postId, userId);
        return ResponseEntity.ok(message);
    }

    // 게시글 좋아요 누른 유저 리스트 조회
    @GetMapping("/{postId}/likes/users")
    public ResponseEntity<List<UserDTO>> getLikers(@PathVariable Integer postId) {
        List<UserDTO> likers = postLikeService.getLikers(postId);
        return ResponseEntity.ok(likers);
    }

}
