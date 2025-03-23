package com.example.javasocialnetwork.exception;

public class GroupAlreadyExistException extends ApiException {
    public GroupAlreadyExistException(String message) {
        super("GROUP_ALREADY_EXISTS", message);
    }
}
