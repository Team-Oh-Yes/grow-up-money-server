package com.ohyes.GrowUpMoney.domain.member.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeartPurchaseRequest {

    @Min(value = 1, message = "최소 1개 이상 구매해야 합니다.")
    @Max(value = 10, message = "최대 10개까지 구매할 수 있습니다.")
    private Integer count;

    public int getCountOrDefault() {
        return count != null ? count : 1;
    }
}