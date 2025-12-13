package com.ohyes.GrowUpMoney.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType {

    TRADEABLE("거래 가능 포인트", "자유롭게 거래할 수 있는 포인트"),
    BOUND("귀속 포인트", "거래 불가능한 포인트");

    private final String displayName;
    private final String description;
}
