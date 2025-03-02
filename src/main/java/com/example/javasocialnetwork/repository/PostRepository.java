package com.example.javasocialnetwork.repository;

import com.example.javasocialnetwork.entity.Posts;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Posts, Long> {
    List<Posts> findByUserId(Long userId);
}