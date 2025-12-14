package com.ohyes.GrowUpMoney.domain.quiz.controller;

import com.ohyes.GrowUpMoney.domain.quiz.dto.request.QuestionCreateRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.request.QuestionUpdateRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.response.QuestionResponse;
import com.ohyes.GrowUpMoney.domain.member.service.HeartService;
import com.ohyes.GrowUpMoney.domain.quiz.service.QuizAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/quiz")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quiz Admin", description = "퀴즈 관리 API (관리자용)")
public class QuizAdminController {

    private final QuizAdminService quizAdminService;
    private final HeartService heartService;

    @Operation(summary = "문제 추가")
    @PostMapping("/question")
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionCreateRequest request) {
        log.info("문제 추가 요청: lessonId={}", request.getLessonId());
        QuestionResponse response = quizAdminService.createQuestion(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "문제 수정")
    @PutMapping("/question/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @Parameter(description = "문제 ID") @PathVariable Long id,
            @Valid @RequestBody QuestionUpdateRequest request) {
        log.info("문제 수정 요청: questionId={}", id);
        QuestionResponse response = quizAdminService.updateQuestion(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "문제 삭제")
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Map<String, Object>> deleteQuestion(
            @Parameter(description = "문제 ID") @PathVariable Long id) {
        log.info("문제 삭제 요청: questionId={}", id);
        quizAdminService.deleteQuestion(id);
        return ResponseEntity.ok(Map.of("message", "문제가 삭제되었습니다.", "deletedId", id));
    }

    @Operation(summary = "문제 상세 조회")
    @GetMapping("/question/{id}")
    public ResponseEntity<QuestionResponse> getQuestion(
            @Parameter(description = "문제 ID") @PathVariable Long id) {
        log.info("문제 상세 조회: questionId={}", id);
        QuestionResponse response = quizAdminService.getQuestion(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "단원별 문제 목록 조회")
    @GetMapping("/questions/lesson/{lessonId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByLesson(
            @Parameter(description = "단원 ID") @PathVariable Long lessonId) {
        log.info("단원별 문제 목록 조회: lessonId={}", lessonId);
        List<QuestionResponse> response = quizAdminService.getQuestionsByLesson(lessonId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "테마별 문제 목록 조회")
    @GetMapping("/questions/theme/{themeId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByTheme(
            @Parameter(description = "테마 ID") @PathVariable Long themeId) {
        log.info("테마별 문제 목록 조회: themeId={}", themeId);
        List<QuestionResponse> response = quizAdminService.getQuestionsByTheme(themeId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "단원 문제 개수 조회")
    @GetMapping("/questions/count/lesson/{lessonId}")
    public ResponseEntity<Map<String, Object>> getQuestionCountByLesson(
            @Parameter(description = "단원 ID") @PathVariable Long lessonId) {
        long count = quizAdminService.getQuestionCountByLesson(lessonId);
        return ResponseEntity.ok(Map.of("lessonId", lessonId, "questionCount", count));
    }

    @Operation(summary = "테마 문제 개수 조회")
    @GetMapping("/questions/count/theme/{themeId}")
    public ResponseEntity<Map<String, Object>> getQuestionCountByTheme(
            @Parameter(description = "테마 ID") @PathVariable Long themeId) {
        long count = quizAdminService.getQuestionCountByTheme(themeId);
        return ResponseEntity.ok(Map.of("themeId", themeId, "questionCount", count));
    }
}