package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.dto.UserWithPostsAndGroupsDto;
import com.example.javasocialnetwork.entity.Group;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "User Controller", description = "Управление пользователями")
@Validated
@RequestMapping("/users")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Массовое создание пользователей",
            description = "Создает несколько пользователей за один запрос, пропуская уже существующие",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователи созданы",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                        "Created 3 users successfully"
                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Невалидные данные в запросе",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                            "errorCode": "VALIDATION_ERROR",
                            "message": "Validation failed",
                            "details": {
                                "users[0].username": "Username must contain only letters and numbers"
                            }
                        }
                    """
                                    )
                            )
                    )
            }
    )
    @PostMapping("/bulk")
    public ResponseEntity<String> bulkCreateUsers(
            @Valid @RequestBody List<User> users
    ) {
        int createdCount = userService.bulkCreateUsers(users).size();
        return ResponseEntity.ok("Created " + createdCount + " users successfully");
    }

    @Operation(
            summary = "Создает нового пользователя",
            description = "Возвращает информацию о успешном создании пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь создан",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                    "User create successfully"
                    """
                                    )
                            )
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
                          "details": { "fieldName": "Username must contain only letters and numbers" }
                        }
                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "USER_ALREADY_EXISTS",
                          "message": "User already exists",
                          "details": { "username": "Antonio" }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<String> registrationUser(
            @Valid @RequestBody User user
    ) {
        LOGGER.debug("Attempting to register user: {}", user.getUserName());
        userService.registration(user);
        return ResponseEntity.ok("User add!!!");
    }

    @GetMapping("/by-post-content")
    @Operation(
            summary = "Поиск пользователей по содержимому поста",
            description = "Возвращает список пользователей, у которых есть посты с указанным содержимым",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный поиск"),
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
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "USER_NOT_FOUND",
                          "message": "User not found",
                          "details": { "userId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<List<UserWithPostsAndGroupsDto>> getUsersByPostContent(
            @Parameter(description = "Содержимое поста для поиска", example = "post")
            @RequestParam String content) {
        List<UserWithPostsAndGroupsDto> users = userService.findByPostContent(content);
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Поиск пользователей по имени",
            description = "Возвращает информацию о пользователе с постами",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный поиск"),
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
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "USER_NOT_FOUND",
                          "message": "User not found",
                          "details": { "userId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }

    )
    public ResponseEntity<List<UserWithPostsAndGroupsDto>> searchUsers(
            @Parameter(description = "Имя пользователя для поиска", example = "Vanya")
            @RequestParam String username
    ) {
        try {
            List<UserWithPostsAndGroupsDto> users = userService.searchUsersByUsername(username);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает информацию о пользователя с группами и постами",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь найден",
                            content = @Content(schema = @Schema(implementation = UserWithPostsAndGroupsDto.class))
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
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "USER_NOT_FOUND",
                          "message": "User not found",
                          "details": { "userId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserWithPostsAndGroupsDto> getOneUserPath(
            @Parameter(description = "Уникальный идентификатор пользователя", example = "1")
            @PathVariable Long id
    ) {
        UserWithPostsAndGroupsDto user = userService.getOne(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Удалить пользователя по ID",
            description = "Возвращает информацию о успешном удалении пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь удален",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                    "User delete successfully"
                    """
                                    )
                            )
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
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "USER_NOT_FOUND",
                          "message": "User not found",
                          "details": { "userId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "Уникальный идентификатор пользователя", example = "1")
            @PathVariable Long id
    ) {
        userService.delete(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @Operation(
            summary = "Добавить пользователя в группу",
            description = "Связывает пользователя с указанной группой",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь успешно добавлен в группу",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                    "User add to group successfully"
                    """
                                    )
                            )
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
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "USER_NOT_FOUND",
                          "message": "User not found",
                          "details": { "userId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @PostMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<String> addUserToGroup(
            @Parameter(description = "ID пользователя", example = "123")
            @PathVariable Long userId,
            @Parameter(description = "ID группы", example = "456")
            @PathVariable Long groupId) {
        userService.addUserToGroup(userId, groupId);
        return ResponseEntity.ok("User added to group!");
    }

    @Operation(
            summary = "Удалить пользователя из группу",
            description = "Удаляет пользователя из указанной группой",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь успешно удален из группы",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                    "User delete from group successfully"
                    """
                                    )
                            )
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
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "USER_NOT_FOUND",
                          "message": "User not found",
                          "details": { "userId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<String> removeUserFromGroup(
            @Parameter(description = "ID пользователя", example = "123")
            @PathVariable Long userId,
            @Parameter(description = "ID группы", example = "456")
            @PathVariable Long groupId) {
        userService.removeUserFromGroup(userId, groupId);
        return ResponseEntity.ok("User removed from group!");
    }

    @Operation(
            summary = "Получить группы пользователя",
            description = "Возвращает все группы, к которым принадлежит пользователь",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список групп успешно получен"),
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
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "USER_NOT_FOUND",
                          "message": "User not found",
                          "details": { "userId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{userId}/groups")
    public ResponseEntity<Set<Group>> getUserGroups(
            @Parameter(description = "ID пользователя", example = "123")
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserGroups(userId));
    }

    @Operation(
            summary = "Обновить пользователя по ID",
            description = "Возвращает информацию о пользователя с группами и постами",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь обновлен",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                    "User update successfully"
                    """
                                    )
                            )
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
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "USER_NOT_FOUND",
                          "message": "User not found",
                          "details": { "userId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @Parameter(description = "ID пользователя", example = "123")
            @PathVariable Long id,
            @Valid @RequestBody
            @Schema(description = "Обновленные данные пользователя", implementation = User.class)
            User updatedUser) {
        userService.updateUser(id, updatedUser);
        return ResponseEntity.ok("User updated successfully!");
    }
}
