package com.ohyes.GrowUpMoney.domain.shop.dto.response;

import com.ohyes.GrowUpMoney.domain.shop.entity.MemberItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemPurchaseResponse {

    private String message;
    private Long memberItemId;
    private String itemName;
    private Integer purchasePrice;
    private Integer remainingBoundPoint;

    public static ItemPurchaseResponse from(MemberItem memberItem, Integer remainingBoundPoint) {
        return new ItemPurchaseResponse(
                "아이템 구매가 완료되었습니다.",
                memberItem.getId(),
                memberItem.getItem().getName(),
                memberItem.getItem().getPrice(),
                remainingBoundPoint
        );
    }
}