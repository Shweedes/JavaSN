package com.example.javasocialnetwork.model;

import com.example.javasocialnetwork.entity.GroupEntity;
import java.util.List;

public class GroupWithUsers {
    private Long id;
    private String name;

    private List<User> users;

    public static GroupWithUsers toModel(GroupEntity entity) {
        GroupWithUsers model = new GroupWithUsers();
        model.setId(entity.getId());
        model.setName(entity.getName());

        // Преобразуем список UserEntity → User
        model.setUsers(entity.getUsers().stream()
                .map(User::toModel)
                .toList());
        return model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
