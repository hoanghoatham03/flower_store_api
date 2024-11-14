package com.example.flowerstore.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String name) {
        super("Not found with: " + name);
    }
}