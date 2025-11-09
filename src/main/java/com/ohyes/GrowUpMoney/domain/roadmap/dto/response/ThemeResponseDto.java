package com.ohyes.GrowUpMoney.domain.roadmap.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeResponseDto {

    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    private Integer totalLessons;
    private Integer completedLessons;
    private Integer progressPercentage;

}
