package com.example.javasocialnetwork.repository;

import com.example.javasocialnetwork.entity.Groups;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Groups, Long> {
    Groups findByName(String name);

    @EntityGraph(attributePaths = "users")
    Optional<Groups> findWithUsersById(Long id);
}
