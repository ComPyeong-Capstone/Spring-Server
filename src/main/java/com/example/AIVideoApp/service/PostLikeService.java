package com.example.AIVideoApp.service;

import com.example.AIVideoApp.dto.PostLikeDTO;
import com.example.AIVideoApp.dto.UserDTO;
import com.example.AIVideoApp.entity.Post;
import com.example.AIVideoApp.entity.PostLike;
import com.example.AIVideoApp.entity.User;
import com.example.AIVideoApp.repository.PostLikeRepository;
import com.example.AIVideoApp.repository.PostRepository;
import com.example.AIVideoApp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostLikeService(PostLikeRepository postLikeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String likePost(Integer postId, Integer userId) {


        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (postOpt.isEmpty() || userOpt.isEmpty()) {
            return "해당 게시글 또는 유저가 존재하지 않습니다.";
        }

        if (postLikeRepository.existsByPostPostIdAndUserUserId(postId, userId)) {
            return "이미 좋아요를 눌렀습니다.";
        }

        PostLike postLike = new PostLike();
        postLike.setPost(postOpt.get());
        postLike.setUser(userOpt.get());

        postLikeRepository.save(postLike);
        return "좋아요를 눌렀습니다.";
    }

    @Transactional
    public String unlikePost(Integer postId, Integer userId) {
        if (!postLikeRepository.existsByPostPostIdAndUserUserId(postId, userId)) {
            return "좋아요를 누른 기록이 없습니다.";
        }
        postLikeRepository.deleteByPostPostIdAndUserUserId(postId, userId);
        return "좋아요를 취소했습니다.";
    }

    //좋아요 누른 유저 수 조회
    public long getLikeCount(Integer postId) {
        return postLikeRepository.countByPost_PostId(postId);
    }

    //좋아요 누른 유저 목록 조회
    public List<UserDTO> getLikers(Integer postId) {
        List<PostLike> likes = postLikeRepository.findByPostPostId(postId);
        return likes.stream()
                .map(like -> new UserDTO(like.getUser()))
                .collect(Collectors.toList());
    }

}
