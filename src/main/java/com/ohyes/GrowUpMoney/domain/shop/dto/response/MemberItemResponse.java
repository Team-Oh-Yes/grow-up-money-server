package com.ohyes.GrowUpMoney.domain.shop.dto.response;

import com.ohyes.GrowUpMoney.domain.shop.entity.MemberItem;
import com.ohyes.GrowUpMoney.domain.shop.entity.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberItemResponse {

    private Long memberItemId;
    private Long itemId;
    private String itemName;
    private ItemType itemType;
    private String itemTypeDescription;
    private String imageUrl;
    private String itemValue;
    private Boolean isEquipped;
    private LocalDateTime purchasedAt;

    public static MemberItemResponse from(MemberItem memberItem) {
        return new MemberItemResponse(
                memberItem.getId(),
                memberItem.getItem().getId(),
                memberItem.getItem().getName(),
                memberItem.getItem().getItemType(),
                memberItem.getItem().getItemType().getDescription(),
                memberItem.getItem().getImageUrl(),
                memberItem.getItem().getItemValue(),
                memberItem.getIsEquipped(),
                memberItem.getPurchasedAt()
        );
    }
}