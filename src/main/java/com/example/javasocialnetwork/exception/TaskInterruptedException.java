package com.example.javasocialnetwork.exception;

public class TaskInterruptedException extends RuntimeException {
    public TaskInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}