package com.ohyes.GrowUpMoney.domain.gacha.entity;

import com.ohyes.GrowUpMoney.domain.gacha.enums.GachaRewardType;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_gacha_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GachaHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private GachaRewardType rewardType;

    @Column(name = "reward_value")
    private Integer rewardValue;  // 포인트량 또는 NFT ID

    @Column(name = "nft_collection_id")
    private Long nftCollectionId;  // NFT인 경우 collection ID

    @Column(name = "nft_token_id")
    private Long nftTokenId;  // NFT인 경우 발급된 token ID

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public GachaHistory(Member member, GachaRewardType rewardType, Integer rewardValue,
                        Long nftCollectionId, Long nftTokenId) {
        this.member = member;
        this.rewardType = rewardType;
        this.rewardValue = rewardValue;
        this.nftCollectionId = nftCollectionId;
        this.nftTokenId = nftTokenId;
    }

    public String getUsername() {
        return member.getUsername();
    }
}