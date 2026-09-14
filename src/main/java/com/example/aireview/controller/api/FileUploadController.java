package com.example.aireview.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.aireview.dto.UploadResponse;
import com.example.aireview.service.ReviewAnalysisService;
import com.example.aireview.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final ReviewService reviewService;
    private final ReviewAnalysisService reviewAnalysisService;

    @PostMapping("/api/files")
    public ResponseEntity<UploadResponse> upload(@RequestParam("file") MultipartFile file) {
        Long fileId = reviewService.upload(file);
        reviewAnalysisService.analyzeAsync(fileId);
        return ResponseEntity.ok(new UploadResponse(fileId, "ANALYZING"));
    }
}
