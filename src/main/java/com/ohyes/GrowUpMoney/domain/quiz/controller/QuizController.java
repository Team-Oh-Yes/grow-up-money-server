package com.ohyes.GrowUpMoney.domain.quiz.controller;

import com.ohyes.GrowUpMoney.domain.quiz.dto.request.AnswerSubmitRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.response.*;
import com.ohyes.GrowUpMoney.domain.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // 특정 Lesson의 퀴즈 문제 목록 조회
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<QuizListResponse> getQuizList(@PathVariable Long lessonId) {
        QuizListResponse response = quizService.getQuizList(lessonId);
        return ResponseEntity.ok(response);
    }

    // 퀴즈 답안 제출
    @PostMapping("/submit")
    public ResponseEntity<AnswerResultResponse> submitAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AnswerSubmitRequest request
    ) {
        String username = userDetails.getUsername();
        AnswerResultResponse response = quizService.submitAnswer(username, request);
        return ResponseEntity.ok(response);
    }

    // 틀린 문제 목록 조회
    @GetMapping("/wrong-answers")
    public ResponseEntity<WrongAnswerListResponse> getWrongAnswers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long lessonId
    ) {
        String username = userDetails.getUsername();
        WrongAnswerListResponse response = quizService.getWrongAnswers(username, lessonId);
        return ResponseEntity.ok(response);
    }

    // Lesson별 진행도 조회
    @GetMapping("/progress/lessons/{lessonId}")
    public ResponseEntity<LessonQuizSummaryResponse> getLessonProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lessonId
    ) {
        String username = userDetails.getUsername();
        LessonQuizSummaryResponse response = quizService.getLessonProgress(username, lessonId);
        return ResponseEntity.ok(response);
    }
}