package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.dto.PostDto;
import com.example.javasocialnetwork.entity.Posts;
import com.example.javasocialnetwork.exception.PostNotFoundException;
import com.example.javasocialnetwork.exception.UserNotFoundException;
import com.example.javasocialnetwork.service.PostService;
import java.util.List;
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
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<String> createPost(@PathVariable Long userId,
                                        @RequestBody PostDto post) {
        try {
            postService.createPost(userId, post.getContent());
            return ResponseEntity.ok("Post add!!!");
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating post");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Posts>> getUserPosts(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(postService.getUserPosts(userId));
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        try {
            postService.deletePost(postId);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> updatePost(@PathVariable Long postId, @RequestBody PostDto post) {
        try {
            postService.updatePost(postId, post.getContent());
            return ResponseEntity.ok("Post updated successfully");
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating post");
        }
    }
}
