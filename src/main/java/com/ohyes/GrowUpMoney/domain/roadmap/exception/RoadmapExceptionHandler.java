package com.ohyes.GrowUpMoney.domain.roadmap.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.ohyes.GrowUpMoney.domain.roadmap")
@Slf4j
public class RoadmapExceptionHandler {

    // 테마를 찾을 수 없는 경우
    @ExceptionHandler(RoadmapException.ThemeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleThemeNotFound(RoadmapException.ThemeNotFoundException ex) {
        log.error("테마를 찾을 수 없음: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 단원을 찾을 수 없는 경우
    @ExceptionHandler(RoadmapException.LessonNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleLessonNotFound(RoadmapException.LessonNotFoundException ex) {
        log.error("단원을 찾을 수 없음: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 진행 상황을 찾을 수 없는 경우
    @ExceptionHandler(RoadmapException.ProgressNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProgressNotFound(RoadmapException.ProgressNotFoundException ex) {
        log.error("진행 상황을 찾을 수 없음: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 잘못된 진행 상태인 경우
    @ExceptionHandler(RoadmapException.InvalidProgressStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidProgressStatus(RoadmapException.InvalidProgressStatusException ex) {
        log.error("잘못된 진행 상태: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BAD_REQUEST")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 단원이 잠겨있는 경우
    @ExceptionHandler(RoadmapException.LessonNotUnlockedException.class)
    public ResponseEntity<ApiErrorResponse> handleLessonNotUnlocked(RoadmapException.LessonNotUnlockedException ex) {
        log.error("단원 잠금: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("FORBIDDEN")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("잘못된 인자: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BAD_REQUEST")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Validation 실패 처리 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation 실패: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_FAILED")
                .message("입력값 검증에 실패했습니다")
                .details(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllException(Exception ex) {
        log.error("예상치 못한 에러 발생: ", ex);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("서버 내부 오류가 발생했습니다")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}