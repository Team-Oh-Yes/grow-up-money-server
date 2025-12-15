package com.ohyes.GrowUpMoney.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProfileResponse {

    private String username; //유저이름

    private String introduction; //자기소개

    private String profileImageUrl; //프로필 이미지

    private Long favoriteNftId; //대표 예쁘띠id
}
