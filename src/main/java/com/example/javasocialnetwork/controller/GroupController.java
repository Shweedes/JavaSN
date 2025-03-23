package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.dto.GroupWithUsersDto;
import com.example.javasocialnetwork.entity.Group;
import com.example.javasocialnetwork.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<String> registrationGroup(@Valid @RequestBody Group group) {
        groupService.registration(group);
        return ResponseEntity.ok("Group add!!!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupWithUsersDto> getOneGroupPath(@PathVariable Long id) {
        GroupWithUsersDto group = groupService.getOne(id);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok("Group deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody Group updatedGroup
    ) {
        groupService.updateGroup(id, updatedGroup);
        return ResponseEntity.ok("Group updated successfully!");
    }
}

