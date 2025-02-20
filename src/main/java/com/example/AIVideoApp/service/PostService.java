package com.example.AIVideoApp.service;

import com.example.AIVideoApp.dto.PostDTO;
import com.example.AIVideoApp.entity.Post;
import com.example.AIVideoApp.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 🔹 게시물 등록 (DTO 반환)
    public PostDTO createPost(Post post) {
        Post savedPost = postRepository.save(post);
        return new PostDTO(savedPost); // ✅ DTO 변환 생성자 사용
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
