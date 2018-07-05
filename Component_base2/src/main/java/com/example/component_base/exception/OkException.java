package com.example.component_base.exception;

public class OkException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public OkException(String message) {
        super(message);
    }
}
