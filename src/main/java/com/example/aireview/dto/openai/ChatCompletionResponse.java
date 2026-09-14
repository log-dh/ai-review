package com.example.aireview.dto.openai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionResponse {

    private List<Choice> choices;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private ChatMessage message;
    }

    public String firstMessageContent() {
        if (choices == null || choices.isEmpty()) {
            return null;
        }
        return choices.get(0).getMessage().getContent();
    }
}
