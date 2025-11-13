package com.ohyes.GrowUpMoney.domain.roadmap.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProgressResponse {

    private String username;
    private Integer totalThemes;
    private Integer totalLessons;
    private Integer completedLessons;
    private Double overallProgress;
    private Integer totalCorrectCount;
    private Integer totalAttemptedCount;
    private Double overallAccuracy;
    private List<ThemeProgressResponse> themeProgresses;

    @Getter
    @Builder
    public static class ThemeProgressResponse {
        private Long themeId;
        private String themeTitle;
        private Integer orderIndex;
        private Integer totalLessons;
        private Integer completedLessons;
        private Double progressPercentage;
        private Boolean isCompleted;
    }

    public static ProgressResponse of(String username, Integer totalThemes, Integer totalLessons,
                                        Integer completedLessons, Double overallProgress,
                                        Integer totalCorrectCount, Integer totalAttemptedCount,
                                        List<ThemeProgressResponse> themeProgresses) {

        Double overallAccuracy = totalAttemptedCount > 0
                ? (double) totalCorrectCount / totalAttemptedCount * 100
                : 0.0;

        return ProgressResponse.builder()
                .username(username)
                .totalThemes(totalThemes)
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .overallProgress(overallProgress)
                .totalCorrectCount(totalCorrectCount)
                .totalAttemptedCount(totalAttemptedCount)
                .overallAccuracy(overallAccuracy)
                .themeProgresses(themeProgresses)
                .build();
    }

}
