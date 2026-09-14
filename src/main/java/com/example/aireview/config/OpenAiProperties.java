package com.example.aireview.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    private String apiKey;
    private String baseUrl = "https://api.openai.com/v1/chat/completions";
    private String model = "gpt-4o-mini";
    private int maxRetries = 3;
    private int timeoutSeconds = 30;
}
