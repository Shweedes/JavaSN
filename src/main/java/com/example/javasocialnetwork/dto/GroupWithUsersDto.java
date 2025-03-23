package com.example.javasocialnetwork.dto;

import com.example.javasocialnetwork.entity.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "DTO группы с участниками")
public class GroupWithUsersDto {
    @Schema(description = "Уникальный идентификатор группы", example = "1")
    private Long id;
    @Schema(description = "Название группы", example = "Разработчики")
    private String name;
    @Schema(description = "Список участников группы")
    private List<UserDto> users;

    public static GroupWithUsersDto toModel(Group entity) {
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
