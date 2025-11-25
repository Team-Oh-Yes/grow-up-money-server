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
    private String username;
    private Integer rank;
    private Integer totalEarnedPoints;
    private String tier;

    private Boolean isTopThree;

    public static RankingResponse from(Long userId, String username, Integer rank,
                                       Integer totalEarnedPoints, String tier) {
        return RankingResponse.builder()
                .userId(userId)
                .username(username)
                .rank(rank)
                .totalEarnedPoints(totalEarnedPoints)
                .tier(tier)
                .isTopThree(rank <= 3)
                .build();
    }
}
