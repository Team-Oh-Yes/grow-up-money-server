package com.ohyes.GrowUpMoney.domain.member.dto.response;

import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.LessonResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StatisticsResponse {

    // 사용자 기본 정보
    private String username;
    private Integer pointBalance;
    private Integer boundPoint;
    private Integer totalEarnedBoundPoint;
    private Integer totalEarnedPoints;
    private Integer hearts;
    private String tier;
    private Integer userRank;

    // 전체 진행도
    private Double overallProgress;
    private Integer totalThemes;
    private Integer totalLessons;
    private Integer completedLessons;
    private Double percentage;

    // 테마별 진행도
    private List<ThemeProgress> themeProgresses;

    // 현재 진행 중인 단원
    private CurrentLessonInfo currentLesson;

    // 퀴즈 통계
    private Integer totalCorrect;
    private Integer totalAttempted;

    @Getter
    @Builder
    public static class ThemeProgress {
        private Long themeId;
        private String themeTitle;
        private Integer orderIndex;
        private Integer totalLessons;
        private Integer completedLessons;
        private Double progressPercentage;
        private Boolean isCompleted;
    }

    @Getter
    @Builder
    public static class CurrentLessonInfo {
        private Long lessonId;
        private Long themeId;
        private String title;
        private Integer orderIndex;
        private String status;
        private Integer correctCount;
        private Integer totalAttempted;
        private Double accuracy;
    }

}