package com.example.javasocialnetwork.controller;

import com.example.javasocialnetwork.service.LogsService;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
public class LogsController {
    private final LogsService logsService;

    @Autowired
    public LogsController(LogsService logsService) {
        this.logsService = logsService;
    }

    @GetMapping(value = "/api/logs", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<ByteArrayResource> getLogsByDate(
            @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}",
                    message = "Invalid date format (yyyy-MM-dd)") String date
    ) throws IOException {
        LocalDate targetDate = logsService.parseDate(date);
        logsService.validateDateNotInFuture(targetDate);

        List<String> logs = logsService.collectLogs(targetDate);
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
