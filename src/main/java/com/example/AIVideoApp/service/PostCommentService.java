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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 댓글 추가
    public PostCommentDTO addComment(Integer postId, Integer userId, String content, Integer parentCommentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // ✅ 부모 댓글 가져오기 (대댓글일 경우)
        PostComment parentComment = null;
        if (parentCommentId != null) {
            parentComment = postCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        // ✅ PostComment 객체를 빌더 패턴으로 생성
        PostComment comment = PostComment.builder()
                .user(user)
                .post(post)
                .content(content)
                .parent(parentComment)
                .createdAt(LocalDateTime.now())
                .build();

        PostComment savedComment = postCommentRepository.save(comment);
        return new PostCommentDTO(savedComment, userId);
    }

    // 특정 게시물의 댓글 조회
    public List<PostCommentDTO> getCommentsByPostId(Integer postId, Integer currentUserId) {
        List<PostComment> comments = postCommentRepository.findByPost_PostId(postId);

        return comments.stream()
                .map(comment -> new PostCommentDTO(comment, currentUserId)) // ✅ currentUserId 전달
                .collect(Collectors.toList());
    }

    public void deleteComment(Integer postId, Integer commentId, Integer UserId) {
        // 댓글 조회
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // 댓글이 해당 게시글에 속하는지 확인 (안전 체크)
        if (!comment.getPost().getPostId().equals(postId)) {
            throw new RuntimeException("해당 댓글은 이 게시글에 속하지 않습니다.");
        }

        // 댓글 작성자와 게시글 작성자 비교
        Integer commentAuthorId = comment.getUser().getUserId();
        Integer postAuthorId = comment.getPost().getUser().getUserId(); // Post 엔티티에 작성자(user) 필드가 있다고 가정

        if (!UserId.equals(commentAuthorId) && !UserId.equals(postAuthorId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        // 조건을 만족하면 댓글 삭제
        postCommentRepository.deleteById(commentId);
    }

}
