package com.example.javasocialnetwork.controller;

import java.time.LocalDate;
import java.util.Map;
import com.example.javasocialnetwork.service.LogsService;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/logs")
public class LogsController {
    private final LogsService logsService;

    @Autowired
    public LogsController(LogsService logsService) {
        this.logsService = logsService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createLogFileAsync(
            @RequestParam
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
                    message = "Invalid date format (yyyy-MM-dd)")
            String date) {
        LocalDate targetDate = logsService.parseDate(date);
        logsService.validateDateNotInFuture(targetDate);
        String taskId = logsService.createLogFileAsync(date);
        return ResponseEntity.accepted().body(Map.of("taskId", taskId));
    }

    @GetMapping("/{taskId}/status")
    public ResponseEntity<Map<String, Object>> getTaskStatus(@PathVariable String taskId) {
        return ResponseEntity.ok(logsService.getTaskStatus(taskId));
    }

    @GetMapping("/{taskId}/file")
    public ResponseEntity<ByteArrayResource> getLogFile(@PathVariable String taskId) {
        LogsService.LogFileResult result = logsService.getTaskLog(taskId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + result.getFilename())
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(result.getContentLength())
                .body(result.getResource());
    }
}