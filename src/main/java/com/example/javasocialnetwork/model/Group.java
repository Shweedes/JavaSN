package com.example.javasocialnetwork.model;

import com.example.javasocialnetwork.entity.GroupEntity;

public class Group {
    private Long id;
    private String name;

    public static Group toModel(GroupEntity entity) {
        Group model = new Group();
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
