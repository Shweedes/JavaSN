package com.example.javasocialnetwork.exception;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", "Validation failed", errors));
    }

    // Добавляем новый обработчик для ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field, message);
        });
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("CONSTRAINT_VIOLATION", "Validation error", errors));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        HttpStatus status = switch (ex.getErrorCode()) {
            case "USER_NOT_FOUND", "GROUP_NOT_FOUND", "POST_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "USER_ALREADY_EXISTS", "GROUP_ALREADY_EXISTS" -> HttpStatus.CONFLICT; // 409
            default -> HttpStatus.BAD_REQUEST;
        };

        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        ex.getErrorCode(),
                        ex.getMessage(),
                        ex.getDetails()
                )
        );
    }
}
