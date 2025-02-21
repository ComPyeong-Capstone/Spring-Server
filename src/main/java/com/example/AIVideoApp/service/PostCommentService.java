package com.example.AIVideoApp.service;

import com.example.AIVideoApp.dto.PostCommentDTO;
import com.example.AIVideoApp.entity.Post;
import com.example.AIVideoApp.entity.PostComment;
import com.example.AIVideoApp.entity.User;
import com.example.AIVideoApp.repository.PostCommentRepository;
import com.example.AIVideoApp.repository.PostRepository;
import com.example.AIVideoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    // 댓글 추가
    public PostCommentDTO addComment(Integer postId, Integer userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostComment comment = new PostComment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);

        PostComment savedComment = postCommentRepository.save(comment);
        return new PostCommentDTO(savedComment);
    }

    // 특정 게시물의 댓글 조회
    public List<PostCommentDTO> getCommentsByPostId(Integer postId) {
        List<PostComment> comments = postCommentRepository.findByPost_PostId(postId);
        return comments.stream().map(PostCommentDTO::new).collect(Collectors.toList());
    }

    // 댓글 삭제
    public void deleteComment(Integer commentId) {
        if (!postCommentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        postCommentRepository.deleteById(commentId);
    }

}
