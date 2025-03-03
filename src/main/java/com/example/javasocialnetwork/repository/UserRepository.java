package com.example.javasocialnetwork.repository;

import com.example.javasocialnetwork.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCase(String username);

    @EntityGraph(attributePaths = {"posts", "groups"})
    Optional<User> findWithPostsAndGroupsById(Long id);
}
