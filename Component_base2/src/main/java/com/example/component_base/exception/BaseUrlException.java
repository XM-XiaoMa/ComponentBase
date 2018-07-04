package com.example.component_base.exception;

public class BaseUrlException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BaseUrlException(String message) {
        super(message);
    }
}
