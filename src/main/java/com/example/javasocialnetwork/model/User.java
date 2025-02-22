package com.example.javasocialnetwork.model;

import com.example.javasocialnetwork.entity.UserEntity;
import java.util.List;

public class User {
    private Long id;
    private String username;
    private List<Post> posts; // Список постов с id и content

    public static User toModel(UserEntity entity) {
        User model = new User();
        model.setId(entity.getId());
        model.setUsername(entity.getUserName());

        // Преобразуем список PostEntity → Post
        model.setPosts(entity.getPosts().stream()
                .map(Post::toModel)
                .toList());
        return model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
