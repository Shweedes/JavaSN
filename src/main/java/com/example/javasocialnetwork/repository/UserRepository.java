package com.example.javasocialnetwork.repository;

import com.example.javasocialnetwork.entity.Users;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);

    List<Users> findByUsernameContainingIgnoreCase(String username);

    @EntityGraph(attributePaths = {"posts", "groups"})
    Optional<Users> findWithPostsAndGroupsById(Long id);
}
