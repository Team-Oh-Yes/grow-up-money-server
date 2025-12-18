package com.ohyes.GrowUpMoney.domain.ranking.dto.response;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RankingResponse{
    private Long userId;
    private String displayName;
    private Integer rank;
    private Integer totalEarnedPoints;
    private String profileUrl;
    private String tier;

    private Boolean isTopThree;

    public static RankingResponse from(Long userId, String displayName, Integer rank,
                                       Integer totalEarnedPoints, String profileUrl,String tier) {
        return RankingResponse.builder()
                .userId(userId)
                .displayName(displayName)
                .rank(rank)
                .totalEarnedPoints(totalEarnedPoints)
                .tier(tier)
                .profileUrl(profileUrl)
                .isTopThree(rank <= 3)
                .build();
    }

    //오버로딩
    public static RankingResponse from(Long userId, String displayName, Integer rank,
                                       Integer totalEarnedPoints,String tier) {
        return RankingResponse.builder()
                .userId(userId)
                .displayName(displayName)
                .rank(rank)
                .totalEarnedPoints(totalEarnedPoints)
                .tier(tier)
                .isTopThree(rank <= 3)
                .build();
    }
}
