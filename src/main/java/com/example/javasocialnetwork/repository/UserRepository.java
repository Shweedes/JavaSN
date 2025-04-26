package com.example.javasocialnetwork.repository;

import com.example.javasocialnetwork.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCase(String username);

    @EntityGraph(attributePaths = {"posts", "groups"})
    Optional<User> findWithPostsAndGroupsById(Long id);

    @Query("SELECT DISTINCT u FROM User u JOIN u.posts p WHERE p.content LIKE %:content%")
    List<User> findAllByPostContent(@Param("content") String content);

    @Query(value = "SELECT DISTINCT u.* FROM users u JOIN posts p " +
            "ON u.id = p.user_id WHERE p.content LIKE %:content%",
            nativeQuery = true)
    List<User> findAllByPostContentNative(@Param("content") String content);

    boolean existsByUsername(String username);
}