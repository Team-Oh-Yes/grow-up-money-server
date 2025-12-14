package com.ohyes.GrowUpMoney.domain.quiz.controller;

import com.ohyes.GrowUpMoney.domain.quiz.dto.request.AnswerSubmitRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.response.*;
import com.ohyes.GrowUpMoney.domain.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roadmap")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quiz", description = "퀴즈 관련 API")
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "단원 퀴즈 목록 조회")
    @GetMapping("/theme/{themeId}/unit/{unitId}/quiz")
    public ResponseEntity<QuizListResponse> getQuizzesByLesson(
            @Parameter(description = "테마 ID") @PathVariable Long themeId,
            @Parameter(description = "단원 ID") @PathVariable Long unitId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("단원 퀴즈 목록 조회 요청: themeId={}, unitId={}", themeId, unitId);
        QuizListResponse response = quizService.getQuizzesByLesson(unitId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "퀴즈 정답 제출")
    @PostMapping("/quiz/{quizId}/answer")
    public ResponseEntity<QuizResultResponse> submitAnswer(
            @Parameter(description = "퀴즈(문제) ID") @PathVariable Long quizId,
            @Valid @RequestBody AnswerSubmitRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("퀴즈 정답 제출 요청: quizId={}", quizId);
        QuizResultResponse response = quizService.submitAnswer(quizId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "단원별 틀린 문제 조회")
    @GetMapping("/quiz/wrong/{lessonId}")
    public ResponseEntity<WrongAnswerListResponse> getWrongAnswersByLesson(
            @Parameter(description = "단원 ID") @PathVariable Long lessonId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("단원 틀린 문제 조회 요청: lessonId={}", lessonId);
        WrongAnswerListResponse response = quizService.getWrongAnswersByLesson(lessonId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 틀린 문제 조회")
    @GetMapping("/quiz/wrong")
    public ResponseEntity<WrongAnswerListResponse> getAllWrongAnswers(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("전체 틀린 문제 조회 요청");
        WrongAnswerListResponse response = quizService.getAllWrongAnswers(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "단원 퀴즈 요약 조회")
    @GetMapping("/quiz/summary/{lessonId}")
    public ResponseEntity<LessonQuizSummaryResponse> getLessonQuizSummary(
            @Parameter(description = "단원 ID") @PathVariable Long lessonId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("단원 퀴즈 요약 조회 요청: lessonId={}", lessonId);
        LessonQuizSummaryResponse response = quizService.getLessonQuizSummary(lessonId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}