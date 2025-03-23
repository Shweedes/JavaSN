package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.dto.PostDto;
import com.example.javasocialnetwork.entity.Post;
import com.example.javasocialnetwork.service.PostService;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/posts")
@Tag(name = "Post Controller", description = "Управление постами пользователей")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(
            summary = "Создать пост",
            description = "Создает новый пост для указанного пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пост создан",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                    "Post add"
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
    @PostMapping("/user/{userId}")
    public ResponseEntity<String> createPost(
            @Parameter(description = "ID пользователя", example = "123")
            @PathVariable Long userId,
            @Valid @RequestBody
            @Schema(description = "Данные нового поста", implementation = PostDto.class)
            PostDto post) {
        postService.createPost(userId, post.getContent());
        return ResponseEntity.ok("Post add!!!");
    }

    @Operation(
            summary = "Получить посты пользователя",
            description = "Возвращает все посты указанного пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Посты успешно получены"),
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
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getUserPosts(
            @Parameter(description = "ID пользователя", example = "123")
            @PathVariable Long userId) {
        return ResponseEntity.ok(postService.getUserPosts(userId));
    }

    @Operation(
            summary = "Удалить пост",
            description = "Удаляет пост по его идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пост успешно удален",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                    "Post delete"
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
                            description = "Пост не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "POST_NOT_FOUND",
                          "message": "Post not found",
                          "details": { "postId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @Parameter(description = "ID поста", example = "456")
            @PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @Operation(
            summary = "Обновить пост",
            description = "Изменяет содержимое существующего поста",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пост успешно обновлен",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                    "Post update"
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
                            description = "Пост не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "errorCode": "POST_NOT_FOUND",
                          "message": "Post not found",
                          "details": { "postId": 123 }
                        }
                        """
                                    )
                            )
                    )
            }
    )
    @ApiResponse(responseCode = "200", description = "Пост успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Пост не найден")
    @PutMapping("/{postId}")
    public ResponseEntity<String> updatePost(
            @Parameter(description = "ID поста", example = "456")
            @PathVariable Long postId,
            @Valid @RequestBody
            @Schema(description = "Обновленные данные поста", implementation = PostDto.class)
            PostDto post) {
        postService.updatePost(postId, post.getContent());
        return ResponseEntity.ok("Post updated successfully");
    }
}
