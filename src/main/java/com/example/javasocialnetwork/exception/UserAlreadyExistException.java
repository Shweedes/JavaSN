package com.example.javasocialnetwork.exception;

public class UserAlreadyExistException extends ApiException {
    public UserAlreadyExistException(String message) {
        super("USER_ALREADY_EXISTS", message);
    }
}
