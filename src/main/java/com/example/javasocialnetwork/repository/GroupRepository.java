package com.example.javasocialnetwork.repository;

import com.example.javasocialnetwork.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    GroupEntity findByName(String name);
}
