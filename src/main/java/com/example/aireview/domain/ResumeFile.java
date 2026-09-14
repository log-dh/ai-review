package com.example.aireview.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ResumeFile {

    private Long fileId;
    private String originalFilename;
    private String storedFilename;
    private String filePath;
    private String fileType;
    private long fileSize;
    private LocalDateTime uploadedAt;
}
