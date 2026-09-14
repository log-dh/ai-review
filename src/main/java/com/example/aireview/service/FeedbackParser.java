package com.example.aireview.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

import com.example.aireview.domain.Feedback;
import com.example.aireview.domain.Suggestion;
import com.example.aireview.dto.openai.FeedbackJson;
import com.example.aireview.exception.OpenAiApiException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedbackParser {

    private final ObjectMapper objectMapper;

    public ParsedFeedback parse(Long fileId, String jsonContent) {
        try {
            FeedbackJson parsed = objectMapper.readValue(jsonContent, FeedbackJson.class);

            Feedback feedback = new Feedback();
            feedback.setFileId(fileId);
            feedback.setOverallScore(parsed.getOverallScore());
            feedback.setOverallComment(parsed.getOverallComment());

            if (parsed.getJobFit() != null) {
                feedback.setJobFitScore(parsed.getJobFit().getScore());
                feedback.setJobFitComment(parsed.getJobFit().getComment());
            }
            if (parsed.getLogic() != null) {
                feedback.setLogicScore(parsed.getLogic().getScore());
                feedback.setLogicComment(parsed.getLogic().getComment());
            }
            if (parsed.getWriting() != null) {
                feedback.setWritingScore(parsed.getWriting().getScore());
                feedback.setWritingComment(parsed.getWriting().getComment());
            }
            if (parsed.getTypo() != null) {
                feedback.setTypoScore(parsed.getTypo().getScore());
                feedback.setTypoComment(parsed.getTypo().getComment());
            }

            List<Suggestion> suggestions = toSuggestions(parsed.getSuggestions());
            return new ParsedFeedback(feedback, suggestions);
        } catch (Exception e) {
            throw new OpenAiApiException("OpenAI 응답 JSON 파싱에 실패했습니다.", e);
        }
    }

    private List<Suggestion> toSuggestions(List<FeedbackJson.SuggestionJson> suggestionJsons) {
        if (suggestionJsons == null || suggestionJsons.isEmpty()) {
            return Collections.emptyList();
        }
        List<Suggestion> suggestions = new ArrayList<>();
        int order = 0;
        for (FeedbackJson.SuggestionJson json : suggestionJsons) {
            Suggestion suggestion = new Suggestion();
            suggestion.setCategory(json.getCategory());
            suggestion.setOriginalExcerpt(json.getOriginal());
            suggestion.setSuggestionText(json.getSuggestion());
            suggestion.setReason(json.getReason());
            suggestion.setSortOrder(order++);
            suggestions.add(suggestion);
        }
        return suggestions;
    }
}
