package com.example.AIVideoApp.service;

import com.example.AIVideoApp.dto.PostCreateDTO;
import com.example.AIVideoApp.dto.PostVideoDTO;
import com.example.AIVideoApp.dto.PostThumbnailDTO;
import com.example.AIVideoApp.entity.HashTag;
import com.example.AIVideoApp.entity.Post;
import com.example.AIVideoApp.entity.PostHashTag;
import com.example.AIVideoApp.external.FastApiClient;
import com.example.AIVideoApp.repository.HashTagRepository;
import com.example.AIVideoApp.repository.PostRepository;
import com.example.AIVideoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.mock.web.MockMultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final HashTagRepository hashTagRepository;
    private final FastApiClient fastApiClient;
    private final S3Uploader s3Uploader;

    // 🔹 게시물 등록 (DTO 반환)
    @Transactional
    public void createPost(PostCreateDTO postDTO, String videoUrlFromFastAPI) throws IOException {
        MultipartFile videoFile = downloadVideoFromUrl(videoUrlFromFastAPI);

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setUser(userRepository.findById(postDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")));
        post.setUpdateTime(LocalDateTime.now());

        byte[] thumbnailBytes = fastApiClient.requestThumbnail(videoFile);
        String thumbnailUrl = s3Uploader.upload(thumbnailBytes, "post-thumbnails", "jpg");
        String s3VideoUrl = s3Uploader.upload(videoFile, "post-videos");

        post.setThumbnailURL(thumbnailUrl);
        post.setVideoURL(s3VideoUrl);

        List<PostHashTag> postHashTags = postDTO.getHashtags().stream().map(tagName -> {
            HashTag tag = hashTagRepository.findByHashName(tagName)
                    .orElseGet(() -> hashTagRepository.save(HashTag.builder().hashName(tagName).build()));
            return PostHashTag.builder().post(post).hashTag(tag).build();
        }).collect(Collectors.toList());

        post.setPostHashTags(postHashTags);
        postRepository.save(post);
    }

    // 🔹 전체 게시물 조회 (DTO 반환)
    public List<PostThumbnailDTO> getAllPosts() {
        return postRepository.findAllWithUser()
                .stream()
                .map(PostThumbnailDTO::new) // ✅ 한 줄로 DTO 변환
                .collect(Collectors.toList());
    }

    // 🔹 특정 게시물 선택 시 재생
    public PostVideoDTO getPostById(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시물을 찾을 수 없습니다."));
        return new PostVideoDTO(post);
    }

    // 🔹 특정 사용자의 게시물 조회 (DTO 반환)
    public List<PostThumbnailDTO> getPostsByUser(Integer userId) {
        return postRepository.findByUserUserId(userId)
                .stream()
                .map(PostThumbnailDTO::new) // ✅ DTO 변환 생성자 활용
                .collect(Collectors.toList());
    }

    // 🔹 특정 해시태그의 게시물 조회 (DB에서 쿼리문 통해 직접 조회)
    public List<PostThumbnailDTO> getPostsByHashTag(String hashTag) {
        return postRepository.findByHashTagWithUser(hashTag).stream()
                .map(PostThumbnailDTO::new) // ✅ Post → PostDTO 변환
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

    // 🔹 게시물 수정 (메시지 반환)
    @Transactional
    public String updatePost(Integer postId, Integer userId, PostVideoDTO dto) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return "게시물 수정에 실패하였습니다: 게시물을 찾을 수 없습니다.";
        }

        Post post = optionalPost.get();

        // 🔒 작성자 검증
        if (!post.getUser().getUserId().equals(userId)) {
            return "게시물 수정에 실패하였습니다: 작성자만 수정할 수 있습니다.";
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
        return "게시물 수정이 완료되었습니다.";
    }

    private MultipartFile downloadVideoFromUrl(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        try (InputStream inputStream = connection.getInputStream()) {
            byte[] fileBytes = inputStream.readAllBytes();
            return new MockMultipartFile(fileName, fileName, "video/mp4", fileBytes);
        }
    }
}
