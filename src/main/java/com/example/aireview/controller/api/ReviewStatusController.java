package com.example.aireview.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.aireview.domain.Feedback;
import com.example.aireview.domain.ResumeFile;
import com.example.aireview.dto.ReviewStatusResponse;
import com.example.aireview.exception.ResourceNotFoundException;
import com.example.aireview.mapper.FeedbackMapper;
import com.example.aireview.mapper.ResumeFileMapper;
import com.example.aireview.service.ReviewStatusResolver;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewStatusController {

    private final ResumeFileMapper resumeFileMapper;
    private final FeedbackMapper feedbackMapper;

    @GetMapping("/api/reviews/{fileId}/status")
    public ReviewStatusResponse status(@PathVariable Long fileId) {
        ResumeFile resumeFile = resumeFileMapper.findById(fileId);
        if (resumeFile == null) {
            throw new ResourceNotFoundException("파일을 찾을 수 없습니다: " + fileId);
        }

        Feedback feedback = feedbackMapper.findByFileId(fileId);
        Integer overallScore = feedback == null ? null : feedback.getOverallScore();
        String status = ReviewStatusResolver.resolve(overallScore);

        return switch (status) {
            case "FAILED" -> ReviewStatusResponse.failed(feedback.getOverallComment());
            case "DONE" -> ReviewStatusResponse.done(overallScore);
            default -> ReviewStatusResponse.analyzing();
        };
    }
}
