package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 한 단원의 퀴즈 진행 요약
public class LessonQuizSummaryResponse {

    private Long lessonId;
    private String lessonTitle;
    private Long themeId;
    private String themeTitle;
    private Integer totalQuestions; // 총 문제 수
    private Integer correctCount;
    private Integer totalEarnedPoints;
    private Integer bonusPoints;
    private Boolean isCompleted;
    private Boolean gachaTicketAwarded;
    private Integer gachaTicketCount;

    public static LessonQuizSummaryResponse of(Long lessonId, String lessonTitle, Long themeId, String themeTitle,
                                               int totalQuestions, int correctCount, int totalEarnedPoints, int bonusPoints, boolean isCompleted,
                                               boolean gachaTicketAwarded, int gachaTicketCount) {
        return LessonQuizSummaryResponse.builder()
                .lessonId(lessonId)
                .lessonTitle(lessonTitle)
                .themeId(themeId)
                .themeTitle(themeTitle)
                .totalQuestions(totalQuestions)
                .correctCount(correctCount)
                .totalEarnedPoints(totalEarnedPoints)
                .bonusPoints(bonusPoints)
                .isCompleted(isCompleted)
                .gachaTicketAwarded(gachaTicketAwarded)
                .gachaTicketCount(gachaTicketCount)
                .build();
    }
}
