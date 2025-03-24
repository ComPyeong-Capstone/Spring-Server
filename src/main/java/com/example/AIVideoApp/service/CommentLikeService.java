package com.example.AIVideoApp.service;

import com.example.AIVideoApp.dto.CommentLikeDTO;
import com.example.AIVideoApp.entity.CommentLike;
import com.example.AIVideoApp.entity.PostComment;
import com.example.AIVideoApp.entity.User;
import com.example.AIVideoApp.enums.NotificationType;
import com.example.AIVideoApp.repository.CommentLikeRepository;
import com.example.AIVideoApp.repository.PostCommentRepository;
import com.example.AIVideoApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 좋아요 추가
    @Transactional
    public CommentLikeDTO likeComment(Integer userId, Integer commentId) {
        // 이미 좋아요 눌렀는지 확인
        if (commentLikeRepository.existsByUser_UserIdAndComment_CommentId(userId, commentId)) {
            throw new RuntimeException("이미 좋아요를 눌렀습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        CommentLike commentLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();

        CommentLike savedLike = commentLikeRepository.save(commentLike);

        // 알림 전송
        if (!userId.equals(comment.getUser().getUserId())) {
            notificationService.createNotification(
                    userId, // sender
                    comment.getUser().getUserId(), // receiver
                    comment.getPost().getPostId(), // postId (댓글이 속한 게시글)
                    NotificationType.COMMENT_LIKE
            );
        }

        return new CommentLikeDTO(savedLike);
    }

    // 좋아요 취소
    @Transactional
    public void unlikeComment(Integer userId, Integer commentId) {
        CommentLike commentLike = commentLikeRepository.findByUser_UserIdAndComment_CommentId(userId, commentId)
                .orElseThrow(() -> new RuntimeException("좋아요를 찾을 수 없습니다."));

        commentLikeRepository.delete(commentLike);
    }

    // 좋아요 개수 조회
    public long countLikes(Integer commentId) {
        return commentLikeRepository.countByComment_CommentId(commentId);
    }

}
