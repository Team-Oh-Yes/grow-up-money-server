package com.ohyes.GrowUpMoney.domain.shop.dto.response;

import com.ohyes.GrowUpMoney.domain.shop.entity.ShopItem;
import com.ohyes.GrowUpMoney.domain.shop.entity.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShopItemResponse {

    private Long id;
    private String name;
    private String description;
    private ItemType itemType;
    private String itemTypeDescription;
    private Integer price;
    private String imageUrl;
    private String itemValue;

    public static ShopItemResponse from(ShopItem item) {
        return new ShopItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getItemType(),
                item.getItemType().getDescription(),
                item.getPrice(),
                item.getImageUrl(),
                item.getItemValue()
        );
    }
}