package com.example.aireview.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import com.example.aireview.exception.TextExtractionException;

@Service
public class TextExtractionService {

    public String extract(Path filePath, String extension) {
        String text = switch (extension.toLowerCase()) {
            case "pdf" -> extractPdf(filePath);
            case "docx" -> extractDocx(filePath);
            default -> throw new TextExtractionException("지원하지 않는 확장자입니다: " + extension);
        };

        if (text == null || text.isBlank()) {
            throw new TextExtractionException("파일에서 텍스트를 추출할 수 없습니다. (이미지 스캔본이거나 빈 문서일 수 있습니다)");
        }
        return text.trim();
    }

    private String extractPdf(Path filePath) {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new TextExtractionException("PDF 텍스트 추출 중 오류가 발생했습니다.", e);
        }
    }

    private String extractDocx(Path filePath) {
        try (InputStream is = Files.newInputStream(filePath);
                XWPFDocument document = new XWPFDocument(is);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        } catch (IOException e) {
            throw new TextExtractionException("DOCX 텍스트 추출 중 오류가 발생했습니다.", e);
        }
    }
}
