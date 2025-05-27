package com.example.AIVideoApp.controller;

import com.example.AIVideoApp.dto.PostCreateDTO;
import com.example.AIVideoApp.dto.PostThumbnailDTO;
import com.example.AIVideoApp.dto.PostVideoDTO;
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
    public ResponseEntity<String> createPost(
            @AuthenticationPrincipal Integer userId,
            @RequestBody PostCreateDTO postDTO
    ) {
        try {
            postDTO.setUserId(userId);
            postService.createPost(postDTO, postDTO.getVideoURL());
            return ResponseEntity.ok("게시물이 성공적으로 등록되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("영상 업로드 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> createPostWithFile(
            @AuthenticationPrincipal Integer userId,
            @RequestPart("postDTO") PostCreateDTO postDTO,
            @RequestPart("videoFile") MultipartFile videoFile
    ) {
        try {
            postDTO.setUserId(userId);
            postService.createPostWithFile(postDTO, videoFile);
            return ResponseEntity.ok("게시물이 성공적으로 등록되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("영상 업로드 중 오류가 발생했습니다.");
        }
    }


    // 2️⃣ 전체 게시물 조회 (GET /posts)
    @GetMapping
    public ResponseEntity<List<PostThumbnailDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts()); // ✅ Post → PostDTO 반환
    }

    // 2️⃣ - 1  정렬 방식에 따라 6개의 게시물 조회 (GET /posts)
    @GetMapping("/sorted")
    public List<PostThumbnailDTO> getAllPosts(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return postService.getPosts(sort, page, size);
    }

    // 게시물 선택 후 재생 요청
    @GetMapping("/{postId}")
    public ResponseEntity<PostVideoDTO> getPostById(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    // 3️⃣ 특정 사용자의 게시물 조회
    @GetMapping("/mine")
    public ResponseEntity<List<PostThumbnailDTO>> getMyPosts(
            @AuthenticationPrincipal Integer userId
    ) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    // 4️⃣ 특정 해시태그를 가진 게시물 조회 (GET /posts?hashtag={hashtag})
    @GetMapping(params = "hashtag")
    public ResponseEntity<List<PostThumbnailDTO>> getPostsByHashTag(@RequestParam String hashtag) {
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
    public ResponseEntity<String> updatePost(
            @AuthenticationPrincipal Integer userId,
            @PathVariable Integer postId,
            @RequestBody PostVideoDTO updatedPost
    ) {
        updatedPost.setAuthor(new UserDTO(userId, null, null));
        String resultMessage = postService.updatePost(postId, userId, updatedPost);

        // 메시지 내용에 따라 상태 코드 유연하게 처리 가능
        if (resultMessage.contains("완료")) {
            return ResponseEntity.ok(resultMessage);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMessage);
        }
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

    // 영상 생성에 사용된 이미지 반환
    @GetMapping("/{postId}/images")
    public ResponseEntity<List<String>> getPostImages(@PathVariable Integer postId) {
        List<String> imageUrls = postService.getImageUrlsByPostId(postId);
        return ResponseEntity.ok(imageUrls);
    }
}
