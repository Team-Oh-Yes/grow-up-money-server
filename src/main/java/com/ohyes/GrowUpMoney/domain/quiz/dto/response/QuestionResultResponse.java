package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResultResponse {
    private Long resultId;
    private Long questionId;
    private Boolean isCorrect;
    private String userAnswer;
    private Timestamp attemptedAt;
    private String explanation;
}
