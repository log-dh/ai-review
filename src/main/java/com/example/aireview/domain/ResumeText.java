package com.example.aireview.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ResumeText {

    private Long textId;
    private Long fileId;
    private String extractedText;
    private LocalDateTime extractedAt;
}
