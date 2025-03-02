package com.example.javasocialnetwork.dto;

import com.example.javasocialnetwork.entity.Posts;

public class PostDto {
    private Long id;
    private String content;

    public static PostDto toModel(Posts entity) {
        PostDto post = new PostDto();
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
