package com.example.javasocialnetwork.dto;

import com.example.javasocialnetwork.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "DTO пользователя с постами и группами")
public class UserWithPostsAndGroupsDto {
    @Schema(hidden = true)
    private Long id;

    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only letters and numbers")
    @Schema(description = "Имя пользователя", example = "John")
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
