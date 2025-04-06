package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.entity.Post;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.example.javasocialnetwork.cache.CacheService;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.example.javasocialnetwork.repository.PostRepository;
import com.example.javasocialnetwork.exception.PostNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    // Исправлено: убрано лишнее подчеркивание
    private static final String USER_POSTS = "user_posts";

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testPost = new Post("Test content", testUser);
        testPost.setId(1L);
    }

    @Test
    void getUserPosts_ShouldReturnCachedPosts() {
        // Arrange
        String cacheKey = USER_POSTS + 1L; // Теперь "user_posts1"
        List<Post> cachedPosts = List.of(testPost);
        when(cacheService.get(cacheKey)).thenReturn(Optional.of(cachedPosts));

        // Act
        List<Post> result = postService.getUserPosts(1L);

        // Assert
        assertThat(result).isEqualTo(cachedPosts);
        verify(postRepository, never()).findByUserId(any());
    }

    @Test
    void getUserPosts_ShouldFetchFromDbWhenCacheEmpty() {
        // Arrange
        String cacheKey = USER_POSTS + 1L; // "user_posts1"
        when(cacheService.get(cacheKey)).thenReturn(Optional.empty());
        when(postRepository.findByUserId(1L)).thenReturn(List.of(testPost));

        // Act
        List<Post> result = postService.getUserPosts(1L);

        // Assert
        assertThat(result).containsExactly(testPost);
        verify(cacheService).put(cacheKey, result);
    }

    @Test
    void getUserPosts_ShouldThrowWhenNoPostsFound() {
        // Arrange
        String cacheKey = USER_POSTS + 1L; // "user_posts1"
        when(cacheService.get(cacheKey)).thenReturn(Optional.empty());
        when(postRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> postService.getUserPosts(1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("No posts found")
                .satisfies(ex -> {
                    assertThat(((PostNotFoundException) ex).getDetails())
                            .containsEntry("userId", 1L);
                });
    }

    @Test
    void createPost_ShouldSaveAndInvalidateCache() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(postRepository.save(any())).thenReturn(testPost);

        // Act
        Post result = postService.createPost(1L, "New post");

        // Assert
        String cacheKey = USER_POSTS + 1L; // "user_posts1"
        assertThat(result).isEqualTo(testPost);
        verify(cacheService).evict(cacheKey);
        verify(cacheService).invalidateUserCache();
    }

    @Test
    void deletePost_ShouldDeleteAndInvalidateCache() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // Act
        postService.deletePost(1L);

        // Assert
        String cacheKey = USER_POSTS + 1L; // "user_posts1"
        verify(postRepository).delete(testPost);
        verify(cacheService).evict(cacheKey);
        verify(cacheService).invalidateUserCache();
    }

    @Test
    void updatePost_ShouldUpdateContentAndCache() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postRepository.save(any())).thenReturn(testPost);

        // Act
        postService.updatePost(1L, "Updated content");

        // Assert
        String cacheKey = USER_POSTS + 1L; // "user_posts1"
        assertThat(testPost.getContent()).isEqualTo("Updated content");
        verify(cacheService).evict(cacheKey);
        verify(cacheService).invalidateUserCache();
        verify(cacheService).evictByPrefix("post_content_");
    }

    @Test
    void updatePost_ShouldThrowWhenPostNotFound() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> postService.updatePost(1L, "Updated content"))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post not found")
                .satisfies(ex -> {
                    assertThat(((PostNotFoundException) ex).getDetails())
                            .containsEntry("postId", 1L);
                });
    }
}