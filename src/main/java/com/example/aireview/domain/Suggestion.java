package com.example.aireview.domain;

import lombok.Data;

@Data
public class Suggestion {

    private Long suggestionId;
    private Long feedbackId;
    private String category;
    private String originalExcerpt;
    private String suggestionText;
    private String reason;
    private Integer sortOrder;
}
