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
public class EncyclopediaResponse {

    private Integer totalNftCount; // 전체 NFT 종류 수
    private Integer acquiredNftCount; // 획득한 NFT 종류 수
    private List<NftEncyclopedia> nftList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NftEncyclopedia {
        private Long collectionId;
        private String nftName;
        private String nftImageUrl;
        private String rarity; // 희귀도
        private boolean isAcquired; // 획득 여부
        private Integer ownedCount; // 보유 개수
    }

}
