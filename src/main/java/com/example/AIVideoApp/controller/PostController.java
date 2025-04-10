package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.PostDTO;
import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.service.PostLikeService;
import com.example.AIVideoApp.service.PostService;
import com.example.AIVideoApp.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final S3Uploader s3Uploader; // 👈 추가


    // 게시물 등록
    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @AuthenticationPrincipal Integer userId,
            @RequestPart("postDTO") PostDTO postDTO,
            @RequestPart("videoFile") MultipartFile videoFile
    ) {
        try {
            postDTO.setAuthor(new UserDTO(userId, null, null));
            PostDTO savedPost = postService.createPost(postDTO, videoFile, s3Uploader);
            return ResponseEntity.ok(savedPost);// 저장된 게시글 정보 반환
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 2️⃣ 전체 게시물 조회 (GET /posts)
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts()); // ✅ Post → PostDTO 반환
    }

    // 3️⃣ 특정 사용자의 게시물 조회
    @GetMapping("/mine")
    public ResponseEntity<List<PostDTO>> getMyPosts(
            @AuthenticationPrincipal Integer userId
    ) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    // 4️⃣ 특정 해시태그를 가진 게시물 조회 (GET /posts?hashtag={hashtag})
    @GetMapping(params = "hashtag")
    public ResponseEntity<List<PostDTO>> getPostsByHashTag(@RequestParam String hashtag) {
        return ResponseEntity.ok(postService.getPostsByHashTag(hashtag)); // ✅ Post → PostDTO 반환
    }

    // 5️⃣ 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @AuthenticationPrincipal Integer userId,
            @PathVariable Integer postId
    ) {
        try {
            postService.deletePost(postId, userId);
            return ResponseEntity.ok("게시물이 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 6️⃣ 게시물 수정 (PUT /posts/{postId})
    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @AuthenticationPrincipal Integer userId,
            @PathVariable Integer postId,
            @RequestBody PostDTO updatedPost
    ) {
        updatedPost.setAuthor(new UserDTO(userId, null, null));
        Optional<PostDTO> updated = postService.updatePost(postId, userId, updatedPost);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 게시글 좋아요 추가
    @PostMapping("/{postId}/likes")
    public ResponseEntity<String> likePost(
            @AuthenticationPrincipal Integer userId,
            @PathVariable Integer postId
    ) {
        String message = postLikeService.likePost(postId, userId);
        return ResponseEntity.ok(message);
    }

    // 게시글 좋아요 취소
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<String> unlikePost(
            @AuthenticationPrincipal Integer userId,
            @PathVariable Integer postId
    ) {
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
