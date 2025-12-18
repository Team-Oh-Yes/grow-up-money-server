package com.ohyes.GrowUpMoney.domain.nft.dto.response;

import com.ohyes.GrowUpMoney.domain.nft.entity.Trade;
import com.ohyes.GrowUpMoney.domain.nft.entity.enums.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeResponse {

    private Long tradeId;
    private Long tokenId;
    private String collectionName;
    private Integer serialNo;
    private String imageUrl;
    private String sellerUsername;
    private String buyerUsername;
    private Integer price;
    private Integer fee;
    private Integer sellerProceeds;  // 판매자가 받을 금액
    private TradeStatus status;
    private String statusDisplayName;
    private LocalDateTime createdAt;
    private LocalDateTime soldAt;

    public static TradeResponse from(Trade trade) {

        String imageUrl = trade.getToken().getCollection().getImage2dUrl();

        return new TradeResponse(
                trade.getId(),
                trade.getToken().getId(),
                trade.getToken().getCollection().getName(),
                trade.getToken().getSerialNo(),
                imageUrl,
                trade.getSeller().getUsername(),
                trade.getBuyer() != null ? trade.getBuyer().getUsername() : null,
                trade.getPrice(),
                trade.getFee(),
                trade.getSellerProceeds(),
                trade.getStatus(),
                trade.getStatus().getDisplayName(),
                trade.getCreatedAt(),
                trade.getSoldAt()
        );
    }
}