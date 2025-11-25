package com.ohyes.GrowUpMoney.domain.nft.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {

    COLLECTION("도감용", "테마 완료 보상으로 받는 거래 불가 NFT"),
    TRADEABLE("거래용", "뽑기로 얻는 거래 가능 NFT");

    private final String displayName;
    private final String description;
}
