package com.example.javasocialnetwork.dto;

import com.example.javasocialnetwork.entity.User;

public class UserDto {
    private Long id;


    private String username;

    public static UserDto toModel(User entity) {
        UserDto model = new UserDto();
        model.setId(entity.getId());
        model.setUsername(entity.getUserName());
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
}
