package com.example.AIVideoApp.repository;

import com.example.AIVideoApp.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    List<PostComment> findByPost_PostId(Integer postId);

}
