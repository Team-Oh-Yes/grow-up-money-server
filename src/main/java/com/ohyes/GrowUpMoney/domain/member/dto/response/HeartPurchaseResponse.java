package com.ohyes.GrowUpMoney.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeartPurchaseResponse {

    private String message;
    private Boolean success;
    private Integer purchasedCount;
    private Integer usedPoints;
    private Integer currentHearts;
    private Integer remainingPoints;

    public static HeartPurchaseResponse success(int purchasedCount, int usedPoints, int currentHearts, int remainingPoints) {
        return HeartPurchaseResponse.builder()
                .message("하트 충전 완료")
                .success(true)
                .purchasedCount(purchasedCount)
                .usedPoints(usedPoints)
                .currentHearts(currentHearts)
                .remainingPoints(remainingPoints)
                .build();
    }

    public static HeartPurchaseResponse fail(String message) {
        return HeartPurchaseResponse.builder()
                .message(message)
                .success(false)
                .build();
    }
}