package com.ohyes.GrowUpMoney.domain.nft.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyNftResponse {

    private List<NftTokenResponse> collectionNfts;  // 도감용 NFT 목록
    private List<NftTokenResponse> tradeableNfts;   // 거래용 NFT 목록
    private Integer totalCollectionCount;            // 도감용 총 개수
    private Integer totalTradeableCount;             // 거래용 총 개수
    private Integer totalCount;                      // 전체 NFT 개수

    public MyNftResponse(List<NftTokenResponse> collectionNfts, List<NftTokenResponse> tradeableNfts) {
        this.collectionNfts = collectionNfts;
        this.tradeableNfts = tradeableNfts;
        this.totalCollectionCount = collectionNfts.size();
        this.totalTradeableCount = tradeableNfts.size();
        this.totalCount = totalCollectionCount + totalTradeableCount;
    }
}