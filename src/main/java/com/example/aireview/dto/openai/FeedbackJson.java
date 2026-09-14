package com.example.aireview.dto.openai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackJson {

    private Integer overallScore;
    private String overallComment;
    private CategoryScore jobFit;
    private CategoryScore logic;
    private CategoryScore writing;
    private CategoryScore typo;
    private List<SuggestionJson> suggestions;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategoryScore {
        private Integer score;
        private String comment;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SuggestionJson {
        private String category;
        private String original;
        private String suggestion;
        private String reason;
    }
}
