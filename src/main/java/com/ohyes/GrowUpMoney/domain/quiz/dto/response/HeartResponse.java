package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

// 유저가 현재 가지고 있는 하트 개수
public class HeartResponse {
    private int hearts; // 현재 보유 하트
}
