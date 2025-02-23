package com.example.javasocialnetwork.repository;

import com.example.javasocialnetwork.entity.GroupEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    GroupEntity findByName(String name);

    @EntityGraph(attributePaths = "users")
    Optional<GroupEntity> findWithUsersById(Long id);
}
