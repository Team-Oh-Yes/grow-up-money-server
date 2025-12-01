package com.ohyes.GrowUpMoney.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteNftResponse {

    private Integer totalFavoriteCount; // 즐겨찾기 총 개수
    private List<FavoriteNft> favoriteNftList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteNft {
        private Long tokenId;
        private Long collectionId;
        private String nftName;
        private String nftImageUrl;
        private Integer serialNumber;
        private String rarity; // 희귀도
        private Boolean isOnSale; // 판매 등록 여부
        private Long currentPrice; // 현재 판매가
    }
}