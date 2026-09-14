package com.example.aireview.controller.api;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.aireview.domain.Feedback;
import com.example.aireview.domain.ResumeFile;
import com.example.aireview.domain.Suggestion;
import com.example.aireview.exception.ResourceNotFoundException;
import com.example.aireview.exception.ReviewNotReadyException;
import com.example.aireview.mapper.FeedbackMapper;
import com.example.aireview.mapper.ResumeFileMapper;
import com.example.aireview.mapper.SuggestionMapper;
import com.example.aireview.service.PdfExportService;
import com.example.aireview.service.ReviewStatusResolver;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewDownloadController {

    private final ResumeFileMapper resumeFileMapper;
    private final FeedbackMapper feedbackMapper;
    private final SuggestionMapper suggestionMapper;
    private final PdfExportService pdfExportService;

    @GetMapping("/api/reviews/{fileId}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long fileId) {
        ResumeFile resumeFile = resumeFileMapper.findById(fileId);
        if (resumeFile == null) {
            throw new ResourceNotFoundException("파일을 찾을 수 없습니다: " + fileId);
        }

        Feedback feedback = feedbackMapper.findByFileId(fileId);
        String status = ReviewStatusResolver.resolve(feedback == null ? null : feedback.getOverallScore());
        if (!"DONE".equals(status)) {
            throw new ReviewNotReadyException("아직 첨삭이 완료되지 않았습니다.");
        }

        List<Suggestion> suggestions = suggestionMapper.findByFeedbackId(feedback.getFeedbackId());
        byte[] pdfBytes = pdfExportService.generate(resumeFile, feedback, suggestions);
        String downloadName = "review_" + fileId + ".pdf";

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(downloadName, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(pdfBytes);
    }
}
