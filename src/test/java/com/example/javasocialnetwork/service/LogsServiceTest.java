package com.example.javasocialnetwork.service;

import static org.assertj.core.api.Assertions.*;
import com.example.javasocialnetwork.exception.ValidationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
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
    void collectLogs_ShouldIgnoreInvalidEntries() throws Exception {
        // Arrange
        Files.write(mainLogFile, List.of(
                "2024-01-15 Valid entry",
                "Invalid entry without date",
                "2024-13-45 Invalid date format",
                ""
        ));

        // Act
        List<String> result = logsService.collectLogs(LocalDate.parse("2024-01-15"));

        // Assert
        assertThat(result).containsExactly("2024-01-15 Valid entry");
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

    @Test
    void getLogFilesInOrder_ShouldSortReverse() throws Exception {
        // Arrange
        Path log1 = tempDir.resolve("app.log.2024-01-10");
        Path log2 = tempDir.resolve("app.log.2024-01-15");
        Path log3 = tempDir.resolve("app.log.2024-01-20");
        Files.createFile(log1);
        Files.createFile(log2);
        Files.createFile(log3);

        // Act
        List<Path> files = logsService.getLogFilesInOrder();

        // Assert
        assertThat(files)
                .extracting(Path::getFileName)
                .containsExactly(
                        Paths.get("app.log.2024-01-20"),
                        Paths.get("app.log.2024-01-15"),
                        Paths.get("app.log.2024-01-10"),
                        mainLogFile.getFileName()
                );
    }
}