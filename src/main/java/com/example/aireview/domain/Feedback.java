package com.example.aireview.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Feedback {

    /** 분석 실패 시 overallScore에 저장하는 sentinel 값 (상태 컬럼이 없어 이 값으로 실패를 표현). */
    public static final int FAILED_SCORE = -1;

    private Long feedbackId;
    private Long fileId;
    private Integer overallScore;
    private String overallComment;
    private Integer jobFitScore;
    private String jobFitComment;
    private Integer logicScore;
    private String logicComment;
    private Integer writingScore;
    private String writingComment;
    private Integer typoScore;
    private String typoComment;
    private LocalDateTime createdAt;
}
