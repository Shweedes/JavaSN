package com.example.javasocialnetwork.exception;

public class PostNotFoundException extends ApiException {
    public PostNotFoundException(String message) {
        super("POST_FOUND", message);
    }
}
