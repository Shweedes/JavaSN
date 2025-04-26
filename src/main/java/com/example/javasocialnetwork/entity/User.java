package com.example.javasocialnetwork.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "users")
@Schema(description = "Сущность пользователя системы")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only letters and numbers")
    @Schema(
            description = "Логин пользователя",
            example = "John",
            minLength = 3,
            maxLength = 50,
            pattern = "^[a-zA-Z0-9]+$"
    )
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull(message = "Password cannot be null")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Password must contain at least one letter and one number")
    @Schema(
            description = "Пароль пользователя",
            example = "john_doe123",
            minLength = 3,
            maxLength = 50,
            pattern = "^[a-zA-Z0-9]+$")
    private String password;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @Schema(hidden = true)
    private Set<Group> groups = new HashSet<>();

    public User() {
        // constructor
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

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public void addGroup(Group group) {
        groups.add(group);
        group.getUsers().add(this);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
        group.getUsers().remove(this);
    }

    @OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.REMOVE },
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(hidden = true)
    private List<Post> posts = new ArrayList<>();

    public List<Post> getPosts() {
        return posts;
    }

    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setUser(null);
    }
}