package com.example.aireview.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.aireview.config.FileStorageProperties;
import com.example.aireview.exception.EmptyFileException;
import com.example.aireview.exception.FileSizeExceededException;
import com.example.aireview.exception.UnsupportedFileTypeException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileValidationService {

    private final FileStorageProperties properties;

    public String validateAndGetExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("빈 파일은 업로드할 수 없습니다.");
        }
        if (file.getSize() > properties.getMaxSizeBytes()) {
            throw new FileSizeExceededException(
                    "파일 크기는 %dMB를 초과할 수 없습니다.".formatted(properties.getMaxSizeBytes() / (1024 * 1024)));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new UnsupportedFileTypeException("파일 확장자를 확인할 수 없습니다.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!properties.getAllowedExtensions().contains(extension)) {
            throw new UnsupportedFileTypeException(
                    "지원하지 않는 파일 형식입니다. (허용: %s)".formatted(String.join(", ", properties.getAllowedExtensions())));
        }
        return extension;
    }
}
