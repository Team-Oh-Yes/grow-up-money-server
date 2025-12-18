package com.ohyes.GrowUpMoney.domain.gacha.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GachaResponse {
    private Integer usedTickets;
    private Integer remainingTickets;
    private List<GachaResultItem> results;
    private Integer totalBoundPointsEarned;
    private Integer totalNftsEarned;

    public static GachaResponse of(Integer usedTickets, Integer remainingTickets,
                                   List<GachaResultItem> results) {
        int boundPoints = results.stream()
                .filter(r -> r.getRewardType() == com.ohyes.GrowUpMoney.domain.gacha.enums.GachaRewardType.BOUND_POINT)
                .mapToInt(GachaResultItem::getRewardValue)
                .sum();

        long nftCount = results.stream()
                .filter(r -> r.getRewardType() == com.ohyes.GrowUpMoney.domain.gacha.enums.GachaRewardType.NFT)
                .count();

        return GachaResponse.builder()
                .usedTickets(usedTickets)
                .remainingTickets(remainingTickets)
                .results(results)
                .totalBoundPointsEarned(boundPoints)
                .totalNftsEarned((int) nftCount)
                .build();
    }
}