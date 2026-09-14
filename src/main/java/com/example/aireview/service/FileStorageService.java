package com.example.aireview.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.aireview.config.FileStorageProperties;
import com.example.aireview.exception.TextExtractionException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageProperties properties;

    public record StoredFile(String storedFilename, Path storedPath) {
    }

    public StoredFile store(MultipartFile file, String extension) {
        try {
            Path uploadDir = Path.of(properties.getUploadDir());
            Files.createDirectories(uploadDir);

            String storedFilename = "%s.%s".formatted(UUID.randomUUID(), extension);
            Path targetPath = uploadDir.resolve(storedFilename).normalize();
            file.transferTo(targetPath);

            return new StoredFile(storedFilename, targetPath);
        } catch (IOException e) {
            throw new TextExtractionException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    public void delete(Path storedPath) {
        try {
            Files.deleteIfExists(storedPath);
        } catch (IOException e) {
            log.warn("파일 삭제 실패: {}", storedPath, e);
        }
    }
}
