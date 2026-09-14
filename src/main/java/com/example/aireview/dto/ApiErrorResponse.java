package com.example.aireview.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String message;

    public static ApiErrorResponse of(int status, String message) {
        return new ApiErrorResponse(LocalDateTime.now(), status, message);
    }
}
