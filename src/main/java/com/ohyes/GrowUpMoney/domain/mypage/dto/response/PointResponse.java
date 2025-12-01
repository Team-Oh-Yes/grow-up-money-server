package com.ohyes.GrowUpMoney.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointResponse {

    private Integer pointBalance;  // NFT 거래 가능한 포인트
    private Integer boundPoint;  // 귀속 포인트 (퀴즈로 획득)
    private Integer totalEarnedPoints;  // 총 누적 포인트
    private Integer dailyEarnedPoints;  // 오늘 획득한 포인트
}