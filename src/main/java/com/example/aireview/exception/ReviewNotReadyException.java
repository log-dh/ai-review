package com.example.aireview.exception;

public class ReviewNotReadyException extends RuntimeException {

    public ReviewNotReadyException(String message) {
        super(message);
    }
}
