package com.example.aireview.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import com.example.aireview.domain.Feedback;
import com.example.aireview.domain.ResumeFile;
import com.example.aireview.domain.Suggestion;
import com.example.aireview.exception.TextExtractionException;

/** PDFBox로 첨삭 결과를 새 PDF 문서로 재생성한다. 한글 출력을 위해 번들된 NanumGothic 폰트를 임베드한다. */
@Service
public class PdfExportService {

    private static final String FONT_RESOURCE = "/fonts/NanumGothic-Regular.ttf";
    private static final float MARGIN = 50f;
    private static final float LINE_HEIGHT = 18f;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] generate(ResumeFile resumeFile, Feedback feedback, List<Suggestion> suggestions) {
        try (PDDocument document = new PDDocument()) {
            PDFont font = loadFont(document);
            Writer writer = new Writer(document, font);

            writer.text("자기소개서 첨삭 결과", 18);
            writer.gap(6);
            writer.text("파일명: " + resumeFile.getOriginalFilename(), 11);
            writer.text("업로드일: " + resumeFile.getUploadedAt().format(DATE_FORMAT), 11);
            writer.gap(12);

            writer.text("종합점수: " + feedback.getOverallScore() + " / 100", 14);
            writer.wrapped(feedback.getOverallComment(), 11);
            writer.gap(12);

            writer.section("직무 적합성", feedback.getJobFitScore(), feedback.getJobFitComment());
            writer.section("논리성", feedback.getLogicScore(), feedback.getLogicComment());
            writer.section("문장력", feedback.getWritingScore(), feedback.getWritingComment());
            writer.section("오탈자", feedback.getTypoScore(), feedback.getTypoComment());

            if (suggestions != null && !suggestions.isEmpty()) {
                writer.gap(6);
                writer.text("수정 제안", 14);
                writer.gap(4);
                for (Suggestion suggestion : suggestions) {
                    writer.text("[" + categoryLabel(suggestion.getCategory()) + "]", 11);
                    writer.wrapped("원문: " + suggestion.getOriginalExcerpt(), 11);
                    writer.wrapped("수정 제안: " + suggestion.getSuggestionText(), 11);
                    writer.wrapped("이유: " + suggestion.getReason(), 11);
                    writer.gap(8);
                }
            }

            writer.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new TextExtractionException("PDF 생성 중 오류가 발생했습니다.", e);
        }
    }

    private String categoryLabel(String category) {
        if (category == null) {
            return "기타";
        }
        return switch (category) {
            case "jobFit" -> "직무 적합성";
            case "logic" -> "논리성";
            case "writing" -> "문장력";
            case "typo" -> "오탈자";
            default -> "기타";
        };
    }

    private PDFont loadFont(PDDocument document) throws IOException {
        try (InputStream fontStream = getClass().getResourceAsStream(FONT_RESOURCE)) {
            if (fontStream == null) {
                throw new IOException("폰트 리소스를 찾을 수 없습니다: " + FONT_RESOURCE);
            }
            return PDType0Font.load(document, fontStream, true);
        }
    }

    /** 페이지 넘김과 줄바꿈을 처리하는 내부 텍스트 작성기. */
    private static class Writer {
        private final PDDocument document;
        private final PDFont font;
        private PDPage page;
        private PDPageContentStream stream;
        private float y;

        Writer(PDDocument document, PDFont font) throws IOException {
            this.document = document;
            this.font = font;
            newPage();
        }

        private void newPage() throws IOException {
            if (stream != null) {
                stream.close();
            }
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            y = page.getMediaBox().getHeight() - MARGIN;
        }

        void section(String title, Integer score, String comment) throws IOException {
            text(title + ": " + (score == null ? "-" : score + "점"), 13);
            wrapped(comment, 11);
            gap(10);
        }

        void gap(float amount) {
            y -= amount;
        }

        void text(String content, float fontSize) throws IOException {
            ensureSpace();
            stream.beginText();
            stream.setFont(font, fontSize);
            stream.newLineAtOffset(MARGIN, y);
            stream.showText(content == null ? "" : content);
            stream.endText();
            y -= LINE_HEIGHT;
        }

        void wrapped(String content, float fontSize) throws IOException {
            for (String line : wrapLines(content == null ? "" : content, fontSize)) {
                text(line, fontSize);
            }
        }

        private List<String> wrapLines(String content, float fontSize) throws IOException {
            float maxWidth = PDRectangle.A4.getWidth() - 2 * MARGIN;
            List<String> lines = new ArrayList<>();
            StringBuilder current = new StringBuilder();

            for (String word : content.split(" ")) {
                String candidate = current.isEmpty() ? word : current + " " + word;
                if (textWidth(candidate, fontSize) > maxWidth && !current.isEmpty()) {
                    lines.add(current.toString());
                    current = new StringBuilder(word);
                } else {
                    current = new StringBuilder(candidate);
                }
            }
            if (!current.isEmpty() || lines.isEmpty()) {
                lines.add(current.toString());
            }
            return lines;
        }

        private float textWidth(String text, float fontSize) throws IOException {
            return font.getStringWidth(text) / 1000 * fontSize;
        }

        private void ensureSpace() throws IOException {
            if (y - LINE_HEIGHT < MARGIN) {
                newPage();
            }
        }

        void close() throws IOException {
            stream.close();
        }
    }
}
