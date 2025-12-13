package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

// 하트 구매 후 하트 양
public class HeartPurchaseResponse {
    private int purchased;  // 구매한 하트량
    private int totalHearts; // 최종 보유 하트
}
