package com.example.javasocialnetwork.repository;

import com.example.javasocialnetwork.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);

    @EntityGraph(attributePaths = {"posts", "groups"})
    Optional<UserEntity> findWithPostsAndGroupsById(Long id);
}
