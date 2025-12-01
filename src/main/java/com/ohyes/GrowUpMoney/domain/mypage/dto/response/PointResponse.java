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

    private Long tradeablePoints; // NFT 거래 가능 포인트
    private Long boundPoints; // 귀속 포인 (퀴즈로 획득)
    private Long totalPoints; // 총 누적 포인트
    private Long dailyEarnedPoints; // 오늘 획득한 포인트

}
