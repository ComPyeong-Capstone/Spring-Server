package com.example.AIVideoApp.service;

import com.example.AIVideoApp.dto.PostDTO;
import com.example.AIVideoApp.entity.HashTag;
import com.example.AIVideoApp.entity.Post;
import com.example.AIVideoApp.entity.PostHashTag;
import com.example.AIVideoApp.repository.HashTagRepository;
import com.example.AIVideoApp.repository.PostRepository;
import com.example.AIVideoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final HashTagRepository hashTagRepository;

    // 🔹 게시물 등록 (DTO 반환)
    @Transactional
    public PostDTO createPost(PostDTO postDTO, MultipartFile videoFile, S3Uploader s3Uploader) throws IOException {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setUser(userRepository.findById(postDTO.getAuthor().getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))); // ✅ 예외 메시지 수정
        post.setUpdateTime(LocalDateTime.now());

        // ✅ S3에 파일 업로드
        String videoUrl = s3Uploader.upload(videoFile, "post-videos");
        post.setVideoURL(videoUrl);

        // ✅ 해시태그 연결
        List<PostHashTag> postHashTags = postDTO.getHashtags().stream().map(tagName -> {
            HashTag tag = hashTagRepository.findByHashName(tagName)
                    .orElseGet(() -> hashTagRepository.save(HashTag.builder().hashName(tagName).build()));
            return PostHashTag.builder()
                    .post(post)
                    .hashTag(tag)
                    .build();
        }).collect(Collectors.toList());

        post.setPostHashTags(postHashTags);
        postRepository.save(post);

        return new PostDTO(post); // 저장된 Post → PostDTO로 변환해서 반환
    }

    // 🔹 전체 게시물 조회 (DTO 반환)
    public List<PostDTO> getAllPosts() {
        return postRepository.findAllWithUser()
                .stream()
                .map(PostDTO::new) // ✅ 한 줄로 DTO 변환
                .collect(Collectors.toList());
    }

    // 🔹 특정 사용자의 게시물 조회 (DTO 반환)
    public List<PostDTO> getPostsByUser(Integer userId) {
        return postRepository.findByUserUserId(userId)
                .stream()
                .map(PostDTO::new) // ✅ DTO 변환 생성자 활용
                .collect(Collectors.toList());
    }

    // 🔹 특정 해시태그의 게시물 조회 (DB에서 쿼리문 통해 직접 조회)
    public List<PostDTO> getPostsByHashTag(String hashTag) {
        return postRepository.findByHashTagWithUser(hashTag).stream()
                .map(PostDTO::new) // ✅ Post → PostDTO 변환
                .collect(Collectors.toList());
    }

    // 🔹 게시물 삭제
    public void deletePost(Integer postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 게시물이 존재하지 않습니다."));

        // 🔒 작성자 검증
        if (!post.getUser().getUserId().equals(userId)) {
            throw new SecurityException("작성자만 게시물을 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    @Transactional
    public Optional<PostDTO> updatePost(Integer postId, Integer userId, PostDTO dto) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) return Optional.empty();

        Post post = optionalPost.get();

        // 🔒 작성자 검증
        if (!post.getUser().getUserId().equals(userId)) {
            throw new SecurityException("작성자만 게시물을 수정할 수 있습니다.");
        }

        // 기존 데이터 수정
        post.setTitle(dto.getTitle());
        post.setVideoURL(dto.getVideoURL());

        // 기존 해시태그 연결 삭제
        post.getPostHashTags().clear();
        postRepository.flush(); // 🔥 삭제 먼저 DB 반영

        // 새로운 해시태그 매핑 추가
        for (String tagName : dto.getHashtags()) {
            HashTag hashTag = hashTagRepository.findByHashName(tagName)
                    .orElseGet(() -> hashTagRepository.save(HashTag.builder().hashName(tagName).build()));

            PostHashTag postHashTag = new PostHashTag(post, hashTag); // 생성자 필요
            post.getPostHashTags().add(postHashTag);
        }

        postRepository.save(post);
        return Optional.of(new PostDTO(post));
    }
}
