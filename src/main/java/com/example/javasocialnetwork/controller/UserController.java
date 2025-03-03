package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.dto.UserWithPostsAndGroupsDto;
import com.example.javasocialnetwork.entity.Group;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.exception.UserAlreadyExistException;
import com.example.javasocialnetwork.exception.UserNotFoundException;
import com.example.javasocialnetwork.service.UserService;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> registrationUser(@RequestBody User user) {
        try {
            userService.registration(user);
            return ResponseEntity.ok("User add!!!");
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error");
        }
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
        try {
            UserWithPostsAndGroupsDto user = userService.getOne(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error");
        }
    }

    @PostMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<String> addUserToGroup(@PathVariable Long userId,
                                                 @PathVariable Long groupId) {
        try {
            userService.addUserToGroup(userId, groupId);
            return ResponseEntity.ok("User added to group!");
        } catch (UserNotFoundException | GroupNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<String> removeUserFromGroup(@PathVariable Long userId,
                                                      @PathVariable Long groupId) {
        try {
            userService.removeUserFromGroup(userId, groupId);
            return ResponseEntity.ok("User removed from group!");
        } catch (UserNotFoundException | GroupNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/groups")
    public ResponseEntity<Set<Group>> getUserGroups(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getUserGroups(userId));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id,
                                             @RequestBody User updatedUser) {
        try {
            userService.updateUser(id, updatedUser);
            return ResponseEntity.ok("User updated successfully!");
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user.");
        }
    }
}
