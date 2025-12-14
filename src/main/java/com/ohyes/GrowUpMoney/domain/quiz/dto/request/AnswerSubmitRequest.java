package com.ohyes.GrowUpMoney.domain.quiz.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// 사용자가 퀴즈의 답을 제출
public class AnswerSubmitRequest {
    private Long questionId;
    private String userAnswer;
}
