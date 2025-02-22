package com.example.javasocialnetwork.model;

import com.example.javasocialnetwork.entity.PostEntity;

public class Post {
    private Long id;
    private String content;

    public static Post toModel(PostEntity entity) {
        Post post = new Post();
        post.setId(entity.getId());
        post.setContent(entity.getContent());
        return post;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
