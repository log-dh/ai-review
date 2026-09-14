package com.example.aireview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.aireview.domain.Feedback;
import com.example.aireview.domain.ResumeFile;
import com.example.aireview.domain.ResumeText;
import com.example.aireview.domain.Suggestion;
import com.example.aireview.dto.ReviewHistoryItem;
import com.example.aireview.exception.ResourceNotFoundException;
import com.example.aireview.mapper.FeedbackMapper;
import com.example.aireview.mapper.ResumeFileMapper;
import com.example.aireview.mapper.ResumeTextMapper;
import com.example.aireview.mapper.ReviewHistoryMapper;
import com.example.aireview.mapper.SuggestionMapper;
import com.example.aireview.service.ReviewStatusResolver;
import com.example.aireview.service.TextHighlighter;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewPageController {

    private static final int PAGE_SIZE = 10;

    private final ResumeFileMapper resumeFileMapper;
    private final ResumeTextMapper resumeTextMapper;
    private final FeedbackMapper feedbackMapper;
    private final SuggestionMapper suggestionMapper;
    private final ReviewHistoryMapper reviewHistoryMapper;
    private final TextHighlighter textHighlighter;

    @GetMapping("/")
    public String index() {
        return "redirect:/upload";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @GetMapping("/reviews/{fileId}")
    public String result(@PathVariable Long fileId, Model model) {
        ResumeFile resumeFile = resumeFileMapper.findById(fileId);
        if (resumeFile == null) {
            throw new ResourceNotFoundException("존재하지 않는 파일입니다: " + fileId);
        }
        ResumeText resumeText = resumeTextMapper.findByFileId(fileId);
        Feedback feedback = feedbackMapper.findByFileId(fileId);
        List<Suggestion> suggestions = feedback != null
                ? suggestionMapper.findByFeedbackId(feedback.getFeedbackId())
                : Collections.emptyList();
        String extractedText = resumeText == null ? "" : resumeText.getExtractedText();

        model.addAttribute("resumeFile", resumeFile);
        model.addAttribute("resumeText", resumeText);
        model.addAttribute("feedback", feedback);
        model.addAttribute("suggestions", suggestions);
        model.addAttribute("highlightedText", textHighlighter.highlight(extractedText, suggestions));
        model.addAttribute("status", ReviewStatusResolver.resolve(feedback == null ? null : feedback.getOverallScore()));
        return "result";
    }

    @GetMapping("/reviews")
    public String history(@RequestParam(defaultValue = "1") int page, Model model) {
        int currentPage = Math.max(page, 1);
        int offset = (currentPage - 1) * PAGE_SIZE;

        List<ReviewHistoryItem> items = reviewHistoryMapper.findPage(offset, PAGE_SIZE);
        long totalCount = reviewHistoryMapper.countAll();
        int totalPages = Math.max(1, (int) Math.ceil(totalCount / (double) PAGE_SIZE));

        model.addAttribute("items", items);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        return "history";
    }
}
