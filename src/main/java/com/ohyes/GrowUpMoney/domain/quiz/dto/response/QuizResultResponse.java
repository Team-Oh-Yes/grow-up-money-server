package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 퀴즈 제출 후 결과
public class QuizResultResponse {
    private Long questionId;
    private Boolean isCorrect;
    private String correctAnswer;
    private String userAnswer;
    private Integer awardedPoints;
    private String explanation;
    private Integer remainingHearts;
    private Integer attemptCount;

    public static QuizResultResponse correct(Long questionId, String userAnswer, String correctAnswer,
                                             int awardedPoints, String explanation, int remainingHearts, int attemptCount) {
        return QuizResultResponse.builder()
                .questionId(questionId)
                .isCorrect(true)
                .correctAnswer(correctAnswer)
                .userAnswer(userAnswer)
                .awardedPoints(awardedPoints)
                .explanation(explanation)
                .remainingHearts(remainingHearts)
                .attemptCount(attemptCount)
                .build();
    }

    public static QuizResultResponse incorrect(Long questionId, String userAnswer, String correctAnswer,
                                               String explanation, int remainingHearts, int attemptCount) {
        return QuizResultResponse.builder()
                .questionId(questionId)
                .isCorrect(false)
                .correctAnswer(correctAnswer)
                .userAnswer(userAnswer)
                .awardedPoints(0)
                .explanation(explanation)
                .remainingHearts(remainingHearts)
                .attemptCount(attemptCount)
                .build();
    }

}

