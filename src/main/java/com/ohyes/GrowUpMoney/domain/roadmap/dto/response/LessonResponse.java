package com.ohyes.GrowUpMoney.domain.roadmap.dto.response;

import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.ProgressStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 단원 반환
@Getter
@Builder
public class LessonResponse {

    private Long lessonId;
    private Long themeId;
    private String title;
    private Integer orderIndex;
    private ProgressStatus status;
    private Integer correctCount;
    private Integer totalAttempted;
    private Double accuracy;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    public static LessonResponse from(Lesson lesson) {
        return LessonResponse.builder()
                .lessonId(lesson.getId())
                .themeId(lesson.getTheme().getId())
                .title(lesson.getTitle())
                .orderIndex(lesson.getOrderIndex())
                .status(ProgressStatus.NOT_STARTED)
                .correctCount(0)
                .totalAttempted(0)
                .accuracy(0.0)
                .createdAt(lesson.getCreatedAt())
                .build();
    }

    public static LessonResponse fromWithProgress(Lesson lesson, ProgressStatus status,
                                                  Integer correctCount, Integer totalAttempted,
                                                  Double accuracy, LocalDateTime lastUpdatedAt) {
        return LessonResponse.builder()
                .lessonId(lesson.getId())
                .themeId(lesson.getTheme().getId())
                .title(lesson.getTitle())
                .orderIndex(lesson.getOrderIndex())
                .status(status)
                .correctCount(correctCount)
                .totalAttempted(totalAttempted)
                .accuracy(accuracy)
                .createdAt(lesson.getCreatedAt())
                .lastUpdatedAt(lastUpdatedAt)
                .build();

    }

}
