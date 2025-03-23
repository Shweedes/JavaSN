package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.cache.CacheService;
import com.example.javasocialnetwork.entity.Post;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.exception.NotFoundException;
import com.example.javasocialnetwork.exception.PostNotFoundException;
import com.example.javasocialnetwork.repository.PostRepository;
import com.example.javasocialnetwork.repository.UserRepository;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private static final String USER_POSTS = "user_posts";
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
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

    public List<Post> getUserPosts(Long userId) {
        String cacheKey = USER_POSTS + userId;

        return (List<Post>) cacheService.get(cacheKey)
                .orElseGet(() -> {
                    logger.info("[DB] Fetching posts for user {} from database", userId);

                    List<Post> posts = postRepository.findByUserId(userId);
                    if (posts.isEmpty()) {
                        throw new PostNotFoundException("No posts found")
                                .addDetail("userId", userId);
                    }

                    cacheService.put(cacheKey, posts);
                    return posts;
                });
    }

    // Модифицированные методы с точечной инвалидацией кеша
    public Post createPost(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found")
                        .addDetail("userId", userId));

        Post post = new Post(content, user);
        Post savedPost = postRepository.save(post);

        // Инвалидация кеша
        cacheService.evict(USER_POSTS + userId);
        cacheService.invalidateUserCache();

        return savedPost;
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found")
                        .addDetail("postId", postId));

        Long userId = post.getUser().getId();
        postRepository.delete(post);

        // Инвалидация кеша
        cacheService.evict(USER_POSTS + userId);
        cacheService.invalidateUserCache();
    }

    public void updatePost(Long postId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found")
                        .addDetail("postId", postId));

        Long userId = post.getUser().getId();
        post.setContent(content);
        postRepository.save(post);

        // Инвалидация кеша
        cacheService.evict(USER_POSTS + userId);
        cacheService.invalidateUserCache();
        cacheService.evictByPrefix("post_content_"); // Если есть кеш по контенту
    }
}