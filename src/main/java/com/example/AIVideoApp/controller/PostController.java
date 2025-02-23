package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.PostDTO;
import com.example.AIVideoApp.entity.Post;
import com.example.AIVideoApp.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 1️⃣ 게시물 등록 (POST /posts)ㅇ
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO post) {
        return ResponseEntity.ok(postService.createPost(post)); // ✅ Post → PostDTO 반환
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
        postService.deletePost(postId);
        return ResponseEntity.ok("게시물이 삭제되었습니다.");
    }

    // 6️⃣ 게시물 수정 (PUT /posts/{postId})
    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Integer postId, @RequestBody Post updatedPost) {
        Optional<PostDTO> updated = postService.updatePost(postId, updatedPost); // ✅ Post → PostDTO 반환
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
