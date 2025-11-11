package com.ohyes.GrowUpMoney.domain.roadmap.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoadmapResponse {

    private String username;
    private Integer totalThemes;
    private Double overallProgress;
    private List<ThemeResponse> themes;

    public static RoadmapResponse of(String username, List<ThemeResponse> themes, Double overallProgress) {
        return RoadmapResponse.builder()
                .username(username)
                .totalThemes(themes.size())
                .overallProgress(overallProgress)
                .themes(themes)
                .build();
    }

}
