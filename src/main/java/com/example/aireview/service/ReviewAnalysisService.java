package com.example.aireview.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.aireview.domain.Feedback;
import com.example.aireview.domain.ResumeText;
import com.example.aireview.mapper.FeedbackMapper;
import com.example.aireview.mapper.ResumeTextMapper;
import com.example.aireview.mapper.SuggestionMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 업로드 응답을 먼저 반환한 뒤, 별도 스레드에서 OpenAI 분석을 수행하고 결과를 feedback 테이블에 저장한다.
 * 실패 시에도 Feedback.FAILED_SCORE sentinel로 결과를 저장해 폴링 측에서 실패를 식별할 수 있게 한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewAnalysisService {

    private final ResumeTextMapper resumeTextMapper;
    private final FeedbackMapper feedbackMapper;
    private final SuggestionMapper suggestionMapper;
    private final ReviewPromptService reviewPromptService;
    private final OpenAiClient openAiClient;
    private final FeedbackParser feedbackParser;

    @Async("reviewTaskExecutor")
    public void analyzeAsync(Long fileId) {
        try {
            ResumeText resumeText = resumeTextMapper.findByFileId(fileId);
            if (resumeText == null) {
                throw new IllegalStateException("추출된 텍스트를 찾을 수 없습니다: fileId=" + fileId);
            }

            String content = openAiClient.chat(
                    reviewPromptService.systemPrompt(),
                    reviewPromptService.userPrompt(resumeText.getExtractedText()),
                    true);

            ParsedFeedback parsed = feedbackParser.parse(fileId, content);
            Feedback feedback = parsed.feedback();
            feedbackMapper.insert(feedback);

            if (!parsed.suggestions().isEmpty()) {
                parsed.suggestions().forEach(s -> s.setFeedbackId(feedback.getFeedbackId()));
                suggestionMapper.insertAll(parsed.suggestions());
            }
            log.info("AI 첨삭 완료: fileId={}, overallScore={}", fileId, feedback.getOverallScore());

        } catch (Exception e) {
            log.error("AI 첨삭 실패: fileId={}", fileId, e);
            saveFailure(fileId, e.getMessage());
        }
    }

    private void saveFailure(Long fileId, String message) {
        Feedback failed = new Feedback();
        failed.setFileId(fileId);
        failed.setOverallScore(Feedback.FAILED_SCORE);
        failed.setOverallComment(message != null ? message : "AI 첨삭 처리 중 알 수 없는 오류가 발생했습니다.");
        feedbackMapper.insert(failed);
    }
}
