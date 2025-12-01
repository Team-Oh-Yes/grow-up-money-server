package com.ohyes.GrowUpMoney.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {

    private Integer totalThemeCount; // 전체 테마 수 (10개)
    private Integer completedThemeCount; // 완료한 테마 수
    private Double overallProgress; // 전체 진행률 (%)
    private List<ThemeProgress> themeProgressList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThemeProgress {
        private Long themeId;
        private String themeName;
        private Integer totalUnitCount; // 단원 수 (5개)
        private Integer completedUnitCount; // 완료한 단원 수
        private Double themeProgress; // 테마 진행률 (%)
        private List<UnitProgress> unitProgressList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnitProgress {
        private Long unitId;
        private String unitName;
        private Boolean isCompleted; // 완료 여부
        private Integer quizScore; // 퀴즈 점수
    }

}
