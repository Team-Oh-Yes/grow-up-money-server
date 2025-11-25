package com.ohyes.GrowUpMoney.domain.ranking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRankResponse {

    private RankingResponse myRank; // 내 랭킹 정보
    private List<RankingResponse> nearbyRanks; // 앞뒤 5명씩 (최대 10명)
    private Integer totalUsers; // 전체 사용자 수

    public static UserRankResponse of(RankingResponse myRank,
                                      List<RankingResponse> nearbyRanks,
                                      Integer totalUsers) {
        return UserRankResponse.builder()
                .myRank(myRank)
                .nearbyRanks(nearbyRanks)
                .totalUsers(totalUsers)
                .build();
    }
}