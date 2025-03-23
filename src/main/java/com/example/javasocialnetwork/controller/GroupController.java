package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.dto.GroupWithUsersDto;
import com.example.javasocialnetwork.entity.Group;
import com.example.javasocialnetwork.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Group Controller", description = "Управление группами пользователей")
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(
            summary = "Создать группу",
            description = "Регистрирует новую группу в системе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Группа успешно создана",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                    "Group create successfully"
                    """
                                    )
                            )),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Группа уже существует",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "GROUP_ALREADY_EXISTS",
                          "message": "Group already exists",
                          "details": { "groupName": "As123d" }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<String> registrationGroup(
            @Valid @RequestBody
            @Schema(description = "Данные новой группы", implementation = Group.class)
            Group group) {
        groupService.registration(group);
        return ResponseEntity.ok("Group add!!!");
    }

    @Operation(
            summary = "Получить группу по ID",
            description = "Возвращает информацию о группе с участниками",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Группа найдена",
                            content = @Content(schema = @Schema(implementation = GroupWithUsersDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Невалидные параметры запроса",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "VALIDATION_ERROR",
                          "message": "Validation failed",
                          "details": { "fieldName": "Ошибка валидации" }
                        }
                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Группа не найдена",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "GROUP_NOT_FOUND",
                          "message": "Group not found",
                          "details": { "groupId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<GroupWithUsersDto> getOneGroupPath(
            @Parameter(description = "Уникальный идентификатор группы", example = "1")
            @PathVariable Long id) {
        GroupWithUsersDto group = groupService.getOne(id);
        return ResponseEntity.ok(group);
    }

    @Operation(
            summary = "Удалить группу",
            description = "Удаляет группу по её идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Группа удалена",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                    "Group deleted successfully"
                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Группа не найдена",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "GROUP_NOT_FOUND",
                          "message": "Group not found",
                          "details": { "groupId": 4 }
                        }
                    """
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGroup(
            @Parameter(description = "Уникальный идентификатор группы", example = "1")
            @PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok("Group deleted successfully");
    }

    @Operation(
            summary = "Обновить данные группы",
            description = "Изменяет информацию о существующей группе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные группы обновлены",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                          "Group updated successfully!"
                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Группа не найдена",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "GROUP_NOT_FOUND",
                          "message": "Group not found",
                          "details": { "groupId": 4 }
                        }
                    """
                                    )
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<String> updateGroup(
            @Parameter(description = "Уникальный идентификатор группы", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody
            @Schema(description = "Обновленные данные группы", implementation = Group.class)
            Group updatedGroup) {
        groupService.updateGroup(id, updatedGroup);
        return ResponseEntity.ok("Group updated successfully!");
    }
}

