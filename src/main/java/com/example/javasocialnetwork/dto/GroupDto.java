package com.example.javasocialnetwork.dto;

import com.example.javasocialnetwork.entity.Group;

public class GroupDto {
    private Long id;
    private String name;

    public static GroupDto toModel(Group entity) {
        GroupDto model = new GroupDto();
        model.setId(entity.getId());
        model.setName(entity.getName());

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
}
