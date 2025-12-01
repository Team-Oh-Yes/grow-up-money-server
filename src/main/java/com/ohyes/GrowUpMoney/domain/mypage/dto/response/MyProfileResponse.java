package com.ohyes.GrowUpMoney.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileResponse {

    private Long userId;
    private String username;  // 닉네임 대신 username 사용
    private String email;
    private String profileImageUrl;
    private String introduction;
    private Long favoriteNftId;  // 대표 에쁘띠
    private String favoriteNftName;
    private String favoriteNftImageUrl;
    private String tier;  // 배지 등급
}