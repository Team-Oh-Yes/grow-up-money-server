package com.ohyes.GrowUpMoney.domain.nft.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradePurchaseRequest {

    @NotNull(message = "거래 ID는 필수입니다.")
    private Long tradeId;

}
