package com.example.javasocialnetwork.repository;

import com.example.javasocialnetwork.entity.Group;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findByName(String name);

    @EntityGraph(attributePaths = "users")
    Optional<Group> findWithUsersById(Long id);
}
