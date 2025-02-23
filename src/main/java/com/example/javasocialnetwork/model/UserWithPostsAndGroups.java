package com.example.javasocialnetwork.model;

import com.example.javasocialnetwork.entity.UserEntity;
import java.util.List;

public class UserWithPostsAndGroups {
    private Long id;
    private String username;
    private List<Post> posts; // Список постов с id и content
    private List<Group> groups; // Список постов с id и content

    public static UserWithPostsAndGroups toModel(UserEntity entity) {
        UserWithPostsAndGroups model = new UserWithPostsAndGroups();
        model.setId(entity.getId());
        model.setUsername(entity.getUserName());

        // Преобразуем список PostEntity → Post
        model.setPosts(entity.getPosts().stream()
                .map(Post::toModel)
                .toList());

        // Преобразуем список GroupEntity → Group
        model.setGroups(entity.getGroups().stream()
                .map(Group::toModel)
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

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
