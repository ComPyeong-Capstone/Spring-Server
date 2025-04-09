package com.example.AIVideoApp.repository;

import com.example.AIVideoApp.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // 1️⃣ 특정 사용자의 게시물 조회
    List<Post> findByUserUserId(Integer userId);

    // 2️⃣ 특정 해시태그를 가진 게시물 조회 (매핑 테이블 사용)
    @Query("SELECT p FROM Post p JOIN FETCH p.user JOIN p.postHashTags pht JOIN pht.hashTag h WHERE h.hashName = :hashTag")
    List<Post> findByHashTagWithUser(@Param("hashTag") String hashTag);

    @Query("SELECT p FROM Post p JOIN FETCH p.user")
    List<Post> findAllWithUser();

}
