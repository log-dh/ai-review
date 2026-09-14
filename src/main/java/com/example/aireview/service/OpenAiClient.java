package com.example.aireview.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.example.aireview.config.OpenAiProperties;
import com.example.aireview.dto.openai.ChatCompletionRequest;
import com.example.aireview.dto.openai.ChatCompletionResponse;
import com.example.aireview.dto.openai.ChatMessage;
import com.example.aireview.dto.openai.ResponseFormat;
import com.example.aireview.exception.OpenAiApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring AI 없이 OpenAI Chat Completions API를 RestTemplate으로 직접 호출하는 클라이언트.
 * 429(rate limit)와 timeout/5xx에 대해서만 지수 백오프로 재시도하고, 그 외 오류(401/400 등)는 즉시 실패 처리한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private static final long BASE_BACKOFF_MILLIS = 1000L;

    private final RestTemplate openAiRestTemplate;
    private final OpenAiProperties properties;

    public String chat(String systemPrompt, String userPrompt, boolean jsonMode) {
        ChatCompletionRequest.ChatCompletionRequestBuilder requestBuilder = ChatCompletionRequest.builder()
                .model(properties.getModel())
                .temperature(0.3)
                .messages(List.of(ChatMessage.system(systemPrompt), ChatMessage.user(userPrompt)));

        if (jsonMode) {
            requestBuilder.responseFormat(ResponseFormat.jsonObject());
        }

        HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(requestBuilder.build(), buildHeaders());

        int maxAttempts = Math.max(1, properties.getMaxRetries());
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("OpenAI 호출 시도 {}/{}", attempt, maxAttempts);
                ResponseEntity<ChatCompletionResponse> response = openAiRestTemplate.postForEntity(
                        properties.getBaseUrl(), entity, ChatCompletionResponse.class);

                String content = response.getBody() == null ? null : response.getBody().firstMessageContent();
                if (content == null) {
                    throw new OpenAiApiException("OpenAI 응답에 내용이 없습니다.");
                }
                log.info("OpenAI 호출 성공 (attempt={})", attempt);
                return content;

            } catch (HttpClientErrorException.TooManyRequests | ResourceAccessException | HttpServerErrorException e) {
                log.warn("OpenAI 호출 실패(재시도 대상): attempt={}/{}, error={}", attempt, maxAttempts, e.getMessage());
                if (attempt == maxAttempts) {
                    throw new OpenAiApiException("OpenAI 호출이 %d회 재시도 후에도 실패했습니다.".formatted(maxAttempts), e);
                }
                sleepBackoff(attempt);

            } catch (HttpClientErrorException e) {
                log.error("OpenAI 호출 실패(재시도 불가): status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
                throw new OpenAiApiException("OpenAI 호출 중 오류가 발생했습니다: " + e.getStatusCode(), e);
            }
        }
        throw new OpenAiApiException("OpenAI 호출에 실패했습니다.");
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());
        return headers;
    }

    private void sleepBackoff(int attempt) {
        long backoffMillis = BASE_BACKOFF_MILLIS * (1L << (attempt - 1));
        try {
            Thread.sleep(backoffMillis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new OpenAiApiException("OpenAI 재시도 대기 중 인터럽트가 발생했습니다.", ie);
        }
    }
}
