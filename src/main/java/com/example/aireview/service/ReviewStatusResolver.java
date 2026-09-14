package com.example.aireview.service;

import com.example.aireview.domain.Feedback;

/** feedback 행의 존재 여부/overallScore 값으로 진행 상태(ANALYZING/DONE/FAILED)를 유추한다. */
public final class ReviewStatusResolver {

    private ReviewStatusResolver() {
    }

    public static String resolve(Integer overallScore) {
        if (overallScore == null) {
            return "ANALYZING";
        }
        if (overallScore == Feedback.FAILED_SCORE) {
            return "FAILED";
        }
        return "DONE";
    }
}
