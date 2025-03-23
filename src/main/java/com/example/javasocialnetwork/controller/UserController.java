package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.dto.UserWithPostsAndGroupsDto;
import com.example.javasocialnetwork.entity.Group;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.service.UserService;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> registrationUser(
            @Valid @RequestBody User user
    ) {
        logger.debug("Attempting to register a new user.");
        userService.registration(user);
        return ResponseEntity.ok("User add!!!");
    }

    @GetMapping("/by-post-content")
    public ResponseEntity<List<UserWithPostsAndGroupsDto>> getUsersByPostContent(
            @RequestParam String content) {
        List<UserWithPostsAndGroupsDto> users = userService.findByPostContent(content);
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserWithPostsAndGroupsDto>> searchUsers(@RequestParam String username) {
        try {
            List<UserWithPostsAndGroupsDto> users = userService.searchUsersByUsername(username);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserWithPostsAndGroupsDto> getOneUserPath(@PathVariable Long id) {
        UserWithPostsAndGroupsDto user = userService.getOne(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<String> addUserToGroup(
            @PathVariable Long userId,
            @PathVariable Long groupId) {
        userService.addUserToGroup(userId, groupId);
        return ResponseEntity.ok("User added to group!");
    }

    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<String> removeUserFromGroup(
            @PathVariable Long userId,
            @PathVariable Long groupId) {
        userService.removeUserFromGroup(userId, groupId);
        return ResponseEntity.ok("User removed from group!");
    }

    @GetMapping("/{userId}/groups")
    public ResponseEntity<Set<Group>> getUserGroups(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserGroups(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User updatedUser) {
        userService.updateUser(id, updatedUser);
        return ResponseEntity.ok("User updated successfully!");
    }
}
