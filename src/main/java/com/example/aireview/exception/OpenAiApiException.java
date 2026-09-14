package com.example.aireview.exception;

public class OpenAiApiException extends RuntimeException {

    public OpenAiApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenAiApiException(String message) {
        super(message);
    }
}
