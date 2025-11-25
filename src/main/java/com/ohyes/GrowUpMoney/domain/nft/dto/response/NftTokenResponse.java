package com.ohyes.GrowUpMoney.domain.nft.dto.response;

import com.ohyes.GrowUpMoney.domain.nft.entity.NftToken;
import com.ohyes.GrowUpMoney.domain.nft.entity.enums.Rarity;
import com.ohyes.GrowUpMoney.domain.nft.entity.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NftTokenResponse {

    private Long tokenId;
    private Long collectionId;
    private String collectionName;
    private Integer serialNo;
    private String serialDisplay;
    private TokenType tokenType;
    private String tokenTypeDisplayName;
    private String ownerUsername;
    private Rarity rarity;
    private String rarityDisplayName;
    private String imageUrl;
    private Boolean isOnSale;
    private LocalDateTime mintedAt;

    public static NftTokenResponse from(NftToken token) {
        String imageUrl = token.getTokenType() == TokenType.COLLECTION
                ? token.getCollection().getImage2dUrl()
                : token.getCollection().getImage3dUrl();

        String serialDisplay = token.getSerialNo() != null
                ? token.getSerialNo() + "/" + token.getCollection().getMaxSupply()
                : null;

        return new NftTokenResponse(
                token.getId(),
                token.getCollection().getId(),
                token.getCollection().getName(),
                token.getSerialNo(),
                serialDisplay,
                token.getTokenType(),
                token.getTokenType().getDisplayName(),
                token.getOwner().getUsername(),
                token.getCollection().getRarity(),
                token.getCollection().getRarity().getDisplayName(),
                imageUrl,
                token.getIsOnSale(),
                token.getMintedAt()
        );
    }
}
