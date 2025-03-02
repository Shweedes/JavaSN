package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.entity.Posts;
import com.example.javasocialnetwork.entity.Users;
import com.example.javasocialnetwork.exception.PostNotFoundException;
import com.example.javasocialnetwork.exception.UserNotFoundException;
import com.example.javasocialnetwork.repository.PostRepository;
import com.example.javasocialnetwork.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Posts createPost(Long userId, String content) throws UserNotFoundException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Posts post = new Posts(content, user);
        return postRepository.save(post);
    }

    public List<Posts> getUserPosts(Long userId) throws PostNotFoundException {
        List<Posts> posts = postRepository.findByUserId(userId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException("No posts found for user with id: " + userId);
        }
        return posts;
    }

    public void deletePost(Long postId) throws PostNotFoundException {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post with id " + postId + " not found.");
        }
        postRepository.deleteById(postId);
    }

    public void updatePost(Long postId, String content) throws PostNotFoundException {
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with this id does not exist!!!"));
        post.setContent(content);
        postRepository.save(post);
    }
}