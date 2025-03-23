package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.exception.NotFoundException;
import com.example.javasocialnetwork.service.LogsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Pattern;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/logs")
public class LogsController {
    private final LogsService logsService;

    @Autowired
    public LogsController(LogsService logsService) {
        this.logsService = logsService;
    }

    @Operation(
            summary = "Получить логи за определённую дату",
            description = "Возвращает текстовый файл с логами за указанную дату в формате yyyy-MM-dd",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное получение логов",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string", format = "binary"),
                                    examples = @ExampleObject(
                                            value = """
                                                    [INFO] 2023-10-05 10:00:00 Application started
                                                    [DEBUG] 2023-10-05 10:01:23 Processing request...
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Невалидная дата",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Invalid date format",
                                                    value = """
                                                            {
                                                              "errorCode": "INVALID_DATE_FORMAT",
                                                              "message": "Invalid date format (yyyy-MM-dd)",
                                                              "details": {"date": "2023-13-45"}
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Логи за указанную дату не найдены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Logs not found",
                                                    value = """
                                                            {
                                                              "errorCode": "LOGS_NOT_FOUND",
                                                              "message": "No logs available for the provided date",
                                                              "details": {"date": "2023-10-05"}
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка сервера при чтении логов",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<ByteArrayResource> getLogsByDate(
            @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
                    message = "Invalid date format (yyyy-MM-dd)") String date
    ) throws IOException {
        LocalDate targetDate = logsService.parseDate(date);

        logsService.validateDateNotInFuture(targetDate); // Проверка на будущее
        List<String> logs = logsService.collectLogs(targetDate);

        if (logs.isEmpty()) {
            throw new NotFoundException("No logs available for the provided date", Map.of("date", date));
        }

        String content = String.join("\n", logs);

        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "logs_" + date + ".log";
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(content.getBytes().length)
                .body(resource);
    }
}

