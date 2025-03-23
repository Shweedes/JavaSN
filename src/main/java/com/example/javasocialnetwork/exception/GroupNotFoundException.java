package com.example.javasocialnetwork.exception;

public class GroupNotFoundException extends ApiException {
    public GroupNotFoundException(String message) {
        super("GROUP_NOT_FOUND", message);
    }
}