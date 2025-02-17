package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.entity.GroupEntity;
import com.example.javasocialnetwork.entity.UserEntity;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.exception.UserAlreadyExistException;
import com.example.javasocialnetwork.exception.UserNotFoundException;
import com.example.javasocialnetwork.model.User;
import com.example.javasocialnetwork.service.UserService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> registrationUser(@RequestBody UserEntity user) {
        try {
            userService.registration(user);
            return ResponseEntity.ok("User add!!!");
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error");
        }
    }

    @GetMapping
    public ResponseEntity<User> getOneUserQuery(@RequestParam Long id) {
        try {
            User user = userService.getOne(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            // Если пользователь не найден, возвращаем ошибку 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getOneUserPath(@PathVariable Long id) {
        try {
            User user = userService.getOne(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            // Если пользователь не найден, возвращаем ошибку 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok("User deleted successfully");
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
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<String> removeUserFromGroup(@PathVariable Long userId,
                                                      @PathVariable Long groupId) {
        try {
            userService.removeUserFromGroup(userId, groupId);
            return ResponseEntity.ok("User removed from group!");
        } catch (UserNotFoundException | GroupNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/groups")
    public ResponseEntity<Set<GroupEntity>> getUserGroups(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getUserGroups(userId));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserEntity updatedUser) {
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
