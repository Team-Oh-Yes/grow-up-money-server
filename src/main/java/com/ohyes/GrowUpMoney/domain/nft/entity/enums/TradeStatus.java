package com.ohyes.GrowUpMoney.domain.nft.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TradeStatus {
    LISTING("판매중", "거래소에 등록되어 판매 중인 상태"),
    SOLD("판매완료", "구매자가 구매하여 거래가 완료된 상태"),
    CANCELLED("취소됨", "판매자가 판매를 취소한 상태");

    private final String displayName;
    private final String description;
}
