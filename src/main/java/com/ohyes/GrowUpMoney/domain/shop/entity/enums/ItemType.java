package com.ohyes.GrowUpMoney.domain.shop.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemType {

    PROFILE_FRAME("프로필 액자"),
    NICKNAME_COLOR("닉네임 색상"),
    RANKING_DECOR("랭킹보드 꾸미기");

    private final String description;
}
