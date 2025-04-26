package com.example.javasocialnetwork.exception;

public class DuplicateUserNameException extends RuntimeException {
    public DuplicateUserNameException(String message) {
        super(message);
    }
}