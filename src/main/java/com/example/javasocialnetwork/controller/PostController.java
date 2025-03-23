package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.dto.PostDto;
import com.example.javasocialnetwork.entity.Post;
import com.example.javasocialnetwork.service.PostService;
import java.util.List;
import jakarta.validation.Valid;
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
    public ResponseEntity<String> createPost(
            @PathVariable Long userId,
            @Valid @RequestBody PostDto post
    ) {
        postService.createPost(userId, post.getContent());
        return ResponseEntity.ok("Post add!!!");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getUserPosts(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getUserPosts(userId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostDto post
    ) {
        postService.updatePost(postId, post.getContent());
        return ResponseEntity.ok("Post updated successfully");
    }
}
