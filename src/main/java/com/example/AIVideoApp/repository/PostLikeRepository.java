package com.example.AIVideoApp.repository;

import com.example.AIVideoApp.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    boolean existsByPostPostIdAndUserUserId(Integer postId, Integer userId);// 특정 게시글의 특정 유저가 좋아요 눌렀는지 확인
    void deleteByPostPostIdAndUserUserId(Integer postId, Integer userId); // 특정 게시글의 특정 유저가 누른 좋아요 삭제
    long countByPostPostId(Integer postId);// 특정 게시글의 좋아요 개수 조회
}
