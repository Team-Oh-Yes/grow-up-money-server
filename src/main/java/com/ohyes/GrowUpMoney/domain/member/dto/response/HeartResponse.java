package com.ohyes.GrowUpMoney.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeartResponse {

    private Integer currentHearts;
    private Integer maxHearts;
    private Integer heartCostPoints;
    private Boolean hasEnough;

    public static HeartResponse of(int currentHearts, int maxHearts, int heartCostPoints) {
        return HeartResponse.builder()
                .currentHearts(currentHearts)
                .maxHearts(maxHearts)
                .heartCostPoints(heartCostPoints)
                .hasEnough(currentHearts > 0)
                .build();
    }
}