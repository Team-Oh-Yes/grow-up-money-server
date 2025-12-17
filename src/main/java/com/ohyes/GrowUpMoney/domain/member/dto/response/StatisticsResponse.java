package com.ohyes.GrowUpMoney.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StatisticsResponse {

    private Integer totalEarnedPoints;
    private Integer userRank;
    private Double percentage;

}
