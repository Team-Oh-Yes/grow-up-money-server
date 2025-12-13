package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnswerResultResponse {
    private Long questionId;           // 문제 ID
    private Boolean isCorrect;         // 정답 여부
    private String correctAnswer;      // 정답 (틀렸을 때만 표시)
    private String explanation;        // 해설
    private Integer remainingHearts;   // 남은 하트 개수
}