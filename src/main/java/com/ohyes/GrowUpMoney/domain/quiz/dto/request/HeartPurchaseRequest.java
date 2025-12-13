package com.ohyes.GrowUpMoney.domain.quiz.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

// 하트 구매량
public class HeartPurchaseRequest {
    private int heartAmount;
}
