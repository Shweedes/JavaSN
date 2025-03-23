package com.example.javasocialnetwork.dto;

import com.example.javasocialnetwork.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO для создания/обновления поста")
public class PostDto {
    @Schema(hidden = true)
    private Long id;
    @Schema(
            description = "Содержимое поста",
            example = "Сегодня прекрасный день для программирования!",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String content;

    public static PostDto toModel(Post entity) {
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
