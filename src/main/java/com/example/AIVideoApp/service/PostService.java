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
    public void createPost(PostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setUser(userRepository.findById(postDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다."))); // ✅ 예외 메시지 수정
        post.setVideoURL(postDTO.getVideoURL());
        post.setUpdateTime(LocalDateTime.now());

        // 🔹 해시태그 처리
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
    }

    // 🔹 전체 게시물 조회 (DTO 반환)
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll()
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
        return postRepository.findByHashTag(hashTag).stream()
                .map(PostDTO::new) // ✅ Post → PostDTO 변환
                .collect(Collectors.toList());
    }

    // 🔹 게시물 삭제
    public void deletePost(Integer postId) {
        // 게시물이 존재하는지 확인
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("삭제할 게시물이 존재하지 않습니다.");
        }

        // 게시물 삭제
        postRepository.deleteById(postId);
    }

    // 🔹 게시물 수정 (존재하는 게시물만 수정)
    public Optional<PostDTO> updatePost(Integer postId, Post updatedPost) {
        return postRepository.findById(postId).map(post -> {
            post.setTitle(updatedPost.getTitle());
            Post savedPost = postRepository.save(post);
            return new PostDTO(savedPost); // ✅ 수정 후 DTO 변환
        });
    }
}
