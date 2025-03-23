package com.example.javasocialnetwork.exception;

import java.util.Map;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }

    public NotFoundException(String message, Map<String, Object> details) {
        super("NOT_FOUND", message, details);
    }
}