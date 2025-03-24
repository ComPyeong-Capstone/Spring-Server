package com.example.AIVideoApp.repository;

import com.example.AIVideoApp.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {

    boolean existsByUser_UserIdAndComment_CommentId(Integer userId, Integer commentId);

    Optional<CommentLike> findByUser_UserIdAndComment_CommentId(Integer userId, Integer commentId);

    long countByComment_CommentId(Integer commentId);
}
