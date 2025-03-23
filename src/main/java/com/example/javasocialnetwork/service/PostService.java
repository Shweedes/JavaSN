package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.cache.CacheService;
import com.example.javasocialnetwork.entity.Post;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.exception.NotFoundException;
import com.example.javasocialnetwork.exception.PostNotFoundException;
import com.example.javasocialnetwork.repository.PostRepository;
import com.example.javasocialnetwork.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CacheService cacheService;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CacheService cacheService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
    }

    public Post createPost(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found")
                        .addDetail("userId", userId));

        Post post = new Post(content, user);
        cacheService.invalidateUserCache();
        return postRepository.save(post);
    }

    public List<Post> getUserPosts(Long userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException("No posts found")
                    .addDetail("userId", userId);
        }
        return posts;
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found")
                        .addDetail("postId", postId));

        postRepository.delete(post);
        cacheService.invalidateUserCache();
    }

    public void updatePost(Long postId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found")
                        .addDetail("postId", postId));

        post.setContent(content);
        postRepository.save(post);
        cacheService.invalidateUserCache();
    }
}