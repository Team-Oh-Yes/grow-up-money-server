package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 특정 단원/테마에서 문제 목록 보여줌
public class QuizListResponse {

    private Long lessonId;
    private String lessonTitle;
    private Long themeId;
    private String themeTitle;
    private Integer totalQuestions;
    private Integer completedQuestions;
    private Integer totalPoints;
    private Integer earnedPoints;
    private Integer remainingHearts;
    private Boolean isCompleted;
    private List<QuestionResponse> questions;

    public static QuizListResponse of(Long lessonId, String lessonTitle, Long themeId, String themeTitle,
                                      List<QuestionResponse> questions, int completedQuestions, int earnedPoints, int remainingHearts) {
        int totalPoints = questions.stream().mapToInt(QuestionResponse::getPointReward).sum();
        boolean isCompleted = completedQuestions >= questions.size();

        return QuizListResponse.builder()
                .lessonId(lessonId)
                .lessonTitle(lessonTitle)
                .themeId(themeId)
                .themeTitle(themeTitle)
                .totalQuestions(questions.size())
                .completedQuestions(completedQuestions)
                .totalPoints(totalPoints)
                .earnedPoints(earnedPoints)
                .remainingHearts(remainingHearts)
                .isCompleted(isCompleted)
                .questions(questions)
                .build();
    }

}
