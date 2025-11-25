package com.ohyes.GrowUpMoney.domain.roadmap.dto.response;

import com.ohyes.GrowUpMoney.domain.roadmap.entity.Theme;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

// 테마 반환
@Getter
@Builder
public class ThemeResponse {

    private Long themeId;
    private String title;
    private Integer orderIndex;
    private Integer totalLessons;
    private Integer completedLessons;
    private Double progressPercentage;
    private List<LessonResponse> lessons;
    private LocalDateTime createdAt;

    public static ThemeResponse from(Theme theme) {
        return ThemeResponse.builder()
                .themeId(theme.getId())
                .title(theme.getTitle())
                .orderIndex(theme.getOrderIndex())
                .totalLessons(theme.getLessons().size())
                .createdAt(theme.getCreatedAt())
                .build();
    }

    public static ThemeResponse from(Theme theme, Integer completedLessons, Double progressPercentage) {
        return ThemeResponse.builder()
                .themeId(theme.getId())
                .title(theme.getTitle())
                .orderIndex(theme.getOrderIndex())
                .totalLessons(theme.getLessons().size())
                .totalLessons(completedLessons)
                .progressPercentage(progressPercentage)
                .createdAt(theme.getCreatedAt())
                .build();
    }

    public static ThemeResponse fromWithLessons(Theme theme, List<LessonResponse> lessons,
                                                Integer completedLessons, Double progressPercentage) {
        return ThemeResponse.builder()
                .themeId(theme.getId())
                .title(theme.getTitle())
                .orderIndex(theme.getOrderIndex())
                .totalLessons(theme.getLessons().size())
                .completedLessons(completedLessons)
                .progressPercentage(progressPercentage)
                .lessons(lessons)
                .createdAt(theme.getCreatedAt())
                .build();
    }

}
