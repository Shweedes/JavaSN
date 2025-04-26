package com.example.javasocialnetwork.dto;

import com.example.javasocialnetwork.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO для отображения информации о пользователе")
public class UserDto {
    @Schema(
            description = "Уникальный идентификатор пользователя",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;
    @Schema(
            description = "Логин пользователя",
            example = "john_doe",
            minLength = 3,
            maxLength = 50
    )
    private String username;

    public static UserDto toModel(User entity) {
        UserDto model = new UserDto();
        model.setId(entity.getId());
        model.setUsername(entity.getUsername());
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
