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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 댓글 저장
    @Transactional
    public PostCommentDTO saveComment(PostCommentDTO postCommentDTO) {
        User user = userRepository.findById(postCommentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postCommentDTO.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostComment postComment = new PostComment();
        postComment.setUser(user);
        postComment.setPost(post);
        postComment.setContent(postCommentDTO.getContent());

        postComment = postCommentRepository.save(postComment);

        postCommentDTO.setCommentId(postComment.getCommentId());
        return postCommentDTO;
    }

    // 특정 게시글의 댓글 조회
    public List<PostCommentDTO> getCommentsByPostId(Integer postId) {
        return postCommentRepository.findByPost_PostId(postId)
                .stream()
                .map(comment -> new PostCommentDTO(
                        comment.getCommentId(),
                        comment.getUser().getUserId(),
                        comment.getPost().getPostId(),
                        comment.getContent()))
                .collect(Collectors.toList());
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Integer commentId) {
        if (!postCommentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        postCommentRepository.deleteById(commentId);
    }
}
