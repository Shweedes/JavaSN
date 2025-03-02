package com.example.javasocialnetwork.dto;

import com.example.javasocialnetwork.entity.Groups;
import java.util.List;

public class GroupWithUsersDto {
    private Long id;
    private String name;

    private List<UserDto> users;

    public static GroupWithUsersDto toModel(Groups entity) {
        GroupWithUsersDto model = new GroupWithUsersDto();
        model.setId(entity.getId());
        model.setName(entity.getName());

        // Преобразуем список UserEntity → User
        model.setUsers(entity.getUsers().stream()
                .map(UserDto::toModel)
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

    public List<UserDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserDto> users) {
        this.users = users;
    }
}
