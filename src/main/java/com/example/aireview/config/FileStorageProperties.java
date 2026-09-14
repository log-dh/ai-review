package com.example.aireview.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    private String uploadDir = "uploads";
    private List<String> allowedExtensions = List.of("pdf", "docx");
    private long maxSizeBytes = 20 * 1024 * 1024;
}
