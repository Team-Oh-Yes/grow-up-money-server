package com.ohyes.GrowUpMoney.domain.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointExchangeResponse {

    private String message;
    private Integer exchangedAmount;
    private Integer remainingBoundPoint;
    private Integer currentPointBalance;
}