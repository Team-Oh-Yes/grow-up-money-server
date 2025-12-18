package com.ohyes.GrowUpMoney.domain.gacha.dto.response;

import com.ohyes.GrowUpMoney.domain.gacha.entity.GachaHistory;
import com.ohyes.GrowUpMoney.domain.gacha.enums.GachaRewardType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GachaHistoryResponse {

    private Long id;
    private String username;
    private GachaRewardType rewardType;
    private Integer rewardValue;
    private Long nftCollectionId;
    private String nftName;
    private Long nftTokenId;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환
    public static GachaHistoryResponse from(GachaHistory history) {
        return GachaHistoryResponse.builder()
                .id(history.getId())
                .username(history.getMember().getUsername())
                .rewardType(history.getRewardType())
                .rewardValue(history.getRewardValue())
                .nftCollectionId(history.getNftCollectionId())
                .nftTokenId(history.getNftTokenId())
                .createdAt(history.getCreatedAt())
                .build();
    }

    // NFT 이름 포함 버전 (NftCollection 조회 필요시)
    public static GachaHistoryResponse from(GachaHistory history, String nftName) {
        return GachaHistoryResponse.builder()
                .id(history.getId())
                .username(history.getMember().getUsername())
                .rewardType(history.getRewardType())
                .rewardValue(history.getRewardValue())
                .nftCollectionId(history.getNftCollectionId())
                .nftName(nftName)
                .nftTokenId(history.getNftTokenId())
                .createdAt(history.getCreatedAt())
                .build();
    }
}