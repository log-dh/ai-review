package com.example.aireview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.example.aireview.dto.ApiErrorResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * JSON API 컨트롤러(controller.api 패키지)에만 적용되는 예외 처리기.
 * JSP 뷰 컨트롤러는 별도로 처리하여 에러 JSON이 뷰 응답에 섞이지 않도록 한다.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.example.aireview.controller.api")
public class GlobalExceptionHandler {

    @ExceptionHandler({UnsupportedFileTypeException.class, EmptyFileException.class, FileSizeExceededException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "업로드 가능한 최대 용량을 초과했습니다."));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(ReviewNotReadyException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReady(ReviewNotReadyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler({TextExtractionException.class, OpenAiApiException.class})
    public ResponseEntity<ApiErrorResponse> handleProcessingError(RuntimeException ex) {
        log.error("처리 중 오류 발생", ex);
        return ResponseEntity.unprocessableEntity()
                .body(ApiErrorResponse.of(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception ex) {
        log.error("예상하지 못한 오류 발생", ex);
        return ResponseEntity.internalServerError()
                .body(ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."));
    }
}
