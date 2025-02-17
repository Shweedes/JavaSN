package com.example.javasocialnetwork.controller;


import com.example.javasocialnetwork.entity.GroupEntity;
import com.example.javasocialnetwork.exception.GroupAlreadyExistException;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    // Добавить группу
    @PostMapping
    public ResponseEntity<String> registrationGroup(@RequestBody GroupEntity group) {
        try {
            groupService.registration(group);
            return ResponseEntity.ok().body("Group add!!!");
        } catch (GroupAlreadyExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupEntity> getOneGroupPath(@PathVariable Long id) {
        try {
            GroupEntity group = groupService.getOne(id);
            return ResponseEntity.ok(group);
        } catch (GroupNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long id) {
        try {
            groupService.deleteGroup(id);
            return ResponseEntity.ok("Group deleted successfully");
        } catch (GroupNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

