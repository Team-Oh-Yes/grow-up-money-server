package com.ohyes.GrowUpMoney.domain.nft.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeRegisterRequest {

    @NotNull(message = "토큰 ID는 필수입니다.")
    private Long tokenId;

    @NotNull(message = "판매 가격은 필수입니다.")
    @Min(value = 100, message = "판매 가격은 100 이상이어야 합니다.")
    private Integer price;

}
