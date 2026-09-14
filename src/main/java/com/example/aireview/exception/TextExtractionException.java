package com.example.aireview.exception;

public class TextExtractionException extends RuntimeException {

    public TextExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TextExtractionException(String message) {
        super(message);
    }
}
