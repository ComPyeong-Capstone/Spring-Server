package com.example.AIVideoApp.repository;

import com.example.AIVideoApp.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Integer> {
    Optional<HashTag> findByHashName(String hashName);
}
