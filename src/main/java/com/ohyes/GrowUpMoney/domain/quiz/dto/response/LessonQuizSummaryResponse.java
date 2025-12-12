package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

// 한 단원의 퀴즈 진행 요약
public class LessonQuizSummaryResponse {

    private Long lessonId;
    private String lessonName;

    private int totalQuestions;     // 총 문제 수
    private int completedQuestions; // 사용자가 푼 문제 수
    private int progressRate;       // 진행률 (%)
}
