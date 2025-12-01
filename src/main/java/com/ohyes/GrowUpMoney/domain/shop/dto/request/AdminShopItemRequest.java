package com.ohyes.GrowUpMoney.domain.shop.dto.request;

import com.ohyes.GrowUpMoney.domain.shop.entity.enums.ItemType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminShopItemRequest {

    @NotBlank(message = "아이템 이름을 입력해주세요.")
    private String name;

    private String description;

    @NotNull(message = "아이템 타입을 선택해주세요.")
    private ItemType itemType;

    @NotNull(message = "가격을 입력해주세요.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    private String imageUrl;

    private String itemValue;
}