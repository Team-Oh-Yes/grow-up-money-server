package com.ohyes.GrowUpMoney.domain.quiz.controller;

import com.ohyes.GrowUpMoney.domain.quiz.dto.request.QuestionCreateRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.request.QuestionUpdateRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.response.QuestionResponse;
import com.ohyes.GrowUpMoney.domain.quiz.service.QuizAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/quiz")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class QuizAdminController {

    private final QuizAdminService quizAdminService;

    // 문제 생성
    @PostMapping("/questions")
    public ResponseEntity<QuestionResponse> createQuestion(
            @RequestBody QuestionCreateRequest request
    ) {
        QuestionResponse response = quizAdminService.createQuestion(request);
        return ResponseEntity.ok(response);
    }

    // 문제 수정
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionUpdateRequest request
    ) {
        QuestionResponse response = quizAdminService.updateQuestion(questionId, request);
        return ResponseEntity.ok(response);
    }

    // 문제 삭제
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        quizAdminService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    // 문제 상세 조회 (정답 포함)
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestionDetail(@PathVariable Long questionId) {
        QuestionResponse response = quizAdminService.getQuestionDetail(questionId);
        return ResponseEntity.ok(response);
    }
}