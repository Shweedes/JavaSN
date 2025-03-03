package com.example.javasocialnetwork.dto;

import com.example.javasocialnetwork.entity.User;
import java.util.List;

public class UserWithPostsAndGroupsDto {
    private Long id;
    private String username;
    private List<PostDto> posts; // Список постов с id и content
    private List<GroupDto> groups; // Список постов с id и content

    public static UserWithPostsAndGroupsDto toModel(User entity) {
        UserWithPostsAndGroupsDto model = new UserWithPostsAndGroupsDto();
        model.setId(entity.getId());
        model.setUsername(entity.getUserName());

        // Преобразуем список PostEntity → Post
        model.setPosts(entity.getPosts().stream()
                .map(PostDto::toModel)
                .toList());

        // Преобразуем список GroupEntity → Group
        model.setGroups(entity.getGroups().stream()
                .map(GroupDto::toModel)
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

    public List<PostDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDto> posts) {
        this.posts = posts;
    }

    public List<GroupDto> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupDto> groups) {
        this.groups = groups;
    }
}
