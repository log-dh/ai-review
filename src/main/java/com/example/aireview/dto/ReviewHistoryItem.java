package com.example.aireview.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReviewHistoryItem {

    private Long fileId;
    private String originalFilename;
    private LocalDateTime uploadedAt;
    private Integer overallScore;
}
