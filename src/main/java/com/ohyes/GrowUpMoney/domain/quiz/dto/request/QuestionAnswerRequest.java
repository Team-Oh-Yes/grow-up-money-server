package com.ohyes.GrowUpMoney.domain.quiz.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerRequest {
    private Long questionId;
    private String userAnswer;
}
