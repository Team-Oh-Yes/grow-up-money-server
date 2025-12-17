package com.ohyes.GrowUpMoney.domain.gacha.dto.response;

import com.ohyes.GrowUpMoney.domain.gacha.enums.GachaRewardType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GachaResultItem {
    private GachaRewardType rewardType;
    private Integer rewardValue;  // 포인트량
    private Long nftCollectionId;
    private String nftName;
    private String nftImageUrl;
    private String nftRarity;
    private Long nftTokenId;  // 발급된 토큰 ID

    public static GachaResultItem boundPoint(Integer points) {
        return GachaResultItem.builder()
                .rewardType(GachaRewardType.BOUND_POINT)
                .rewardValue(points)
                .build();
    }

    public static GachaResultItem nft(Long collectionId, String name, String imageUrl,
                                      String rarity, Long tokenId) {
        return GachaResultItem.builder()
                .rewardType(GachaRewardType.NFT)
                .nftCollectionId(collectionId)
                .nftName(name)
                .nftImageUrl(imageUrl)
                .nftRarity(rarity)
                .nftTokenId(tokenId)
                .build();
    }
}