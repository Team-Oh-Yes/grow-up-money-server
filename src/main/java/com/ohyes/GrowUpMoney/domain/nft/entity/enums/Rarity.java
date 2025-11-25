package com.ohyes.GrowUpMoney.domain.nft.entity.enums;

import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rarity {
    COMMON("일반", 1, 50),
    RARE("레어", 2, 30),
    EPIC("에픽", 3, 15),
    LEGENDARY("전설", 4, 5);

    private final String displayName;
    private final int tier;  // 등급 (1~4)
    private final int probability;  // 뽑기 확률 (%)

    // 등급이 높을수록 가치가 높음
    public boolean isHigherThan(Rarity other) {
        return this.tier > other.tier;
    }
}
