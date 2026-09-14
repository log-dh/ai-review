package com.example.aireview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewStatusResponse {

    private String status;
    private Integer overallScore;
    private String message;

    public static ReviewStatusResponse analyzing() {
        return new ReviewStatusResponse("ANALYZING", null, null);
    }

    public static ReviewStatusResponse done(Integer overallScore) {
        return new ReviewStatusResponse("DONE", overallScore, null);
    }

    public static ReviewStatusResponse failed(String message) {
        return new ReviewStatusResponse("FAILED", null, message);
    }
}
