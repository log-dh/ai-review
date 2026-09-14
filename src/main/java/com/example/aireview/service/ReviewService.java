package com.example.aireview.service;

import java.nio.file.Path;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.aireview.domain.ResumeFile;
import com.example.aireview.domain.ResumeText;
import com.example.aireview.mapper.ResumeFileMapper;
import com.example.aireview.mapper.ResumeTextMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final FileValidationService fileValidationService;
    private final FileStorageService fileStorageService;
    private final TextExtractionService textExtractionService;
    private final ResumeFileMapper resumeFileMapper;
    private final ResumeTextMapper resumeTextMapper;

    /**
     * 파일 검증 → 저장 → 텍스트 추출 → DB 저장까지 하나의 트랜잭션으로 처리한다.
     * 추출 실패 시 DB insert는 롤백되고, 디스크에 저장된 파일도 함께 삭제한다.
     */
    @Transactional
    public Long upload(MultipartFile file) {
        String extension = fileValidationService.validateAndGetExtension(file);
        FileStorageService.StoredFile stored = fileStorageService.store(file, extension);

        try {
            ResumeFile resumeFile = new ResumeFile();
            resumeFile.setOriginalFilename(file.getOriginalFilename());
            resumeFile.setStoredFilename(stored.storedFilename());
            resumeFile.setFilePath(stored.storedPath().toString());
            resumeFile.setFileType(extension);
            resumeFile.setFileSize(file.getSize());
            resumeFileMapper.insert(resumeFile);

            String extractedText = textExtractionService.extract(stored.storedPath(), extension);

            ResumeText resumeText = new ResumeText();
            resumeText.setFileId(resumeFile.getFileId());
            resumeText.setExtractedText(extractedText);
            resumeTextMapper.insert(resumeText);

            log.info("파일 업로드 및 텍스트 추출 완료: fileId={}, originalFilename={}", resumeFile.getFileId(),
                    resumeFile.getOriginalFilename());
            return resumeFile.getFileId();
        } catch (RuntimeException e) {
            cleanup(stored.storedPath());
            throw e;
        }
    }

    private void cleanup(Path storedPath) {
        fileStorageService.delete(storedPath);
    }
}
