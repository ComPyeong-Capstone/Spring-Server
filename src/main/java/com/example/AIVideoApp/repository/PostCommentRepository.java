package com.example.AIVideoApp.repository;

import com.example.AIVideoApp.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    @Query("SELECT pc FROM PostComment pc JOIN FETCH pc.user WHERE pc.post.postId = :postId")
    List<PostComment> findWithUserByPostId(@Param("postId") Integer postId);

}
