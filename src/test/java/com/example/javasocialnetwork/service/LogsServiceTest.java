package com.example.javasocialnetwork.service;

import static org.assertj.core.api.Assertions.*;
import com.example.javasocialnetwork.exception.ValidationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LogsServiceTest {

    @TempDir
    Path tempDir;

    @InjectMocks
    private LogsService logsService;

    private Path mainLogFile;

    @BeforeEach
    void setUp() throws IOException {
        mainLogFile = tempDir.resolve("app.log");
        Files.createFile(mainLogFile);
        ReflectionTestUtils.setField(logsService, "logFilePath", mainLogFile.toString());
    }

    @Test
    void parseDate_ShouldReturnValidDate() {
        // Act
        LocalDate result = logsService.parseDate("2024-01-20");

        // Assert
        assertThat(result).isEqualTo(LocalDate.of(2024, 1, 20));
    }

    @Test
    void parseDate_ShouldThrowForInvalidFormat() {
        assertThatThrownBy(() -> logsService.parseDate("2024/01/20"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid date format");
    }

    @Test
    void validateDateNotInFuture_ShouldAllowPastDates() {
        // Act & Assert (no exception)
        logsService.validateDateNotInFuture(LocalDate.now().minusDays(1));
    }

    @Test
    void validateDateNotInFuture_ShouldThrowForFutureDates() {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        assertThatThrownBy(() -> logsService.validateDateNotInFuture(futureDate))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Date cannot be in the future");
    }
}