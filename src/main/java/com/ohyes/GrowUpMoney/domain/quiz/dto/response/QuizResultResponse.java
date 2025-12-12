package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

// 퀴즈 제출 후 결과
public class QuizResultResponse {
    private int totalQuestions;     // 총 문제 수
    private int correctCount;       // 맞춘 수
    private int wrongCount;         // 틀린 수
    private int reward;             // 보상 포인트/하트 등
}
