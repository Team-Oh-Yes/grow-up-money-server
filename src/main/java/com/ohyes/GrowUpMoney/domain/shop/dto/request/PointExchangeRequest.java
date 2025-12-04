package com.ohyes.GrowUpMoney.domain.shop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointExchangeRequest {

    @NotNull(message = "전환할 포인트 수량을 입력해주세요.")
    @Min(value = 1, message = "최소 1 포인트 이상 전환해야 합니다.")
    private Integer amount;
}