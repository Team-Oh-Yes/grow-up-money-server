package com.ohyes.GrowUpMoney.domain.nft.entity;

import com.ohyes.GrowUpMoney.domain.nft.entity.enums.Rarity;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Theme;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_nft_collection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NftCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rarity rarity;

    @Column(name = "image_2d_url", nullable = false)
    private String image2dUrl;

    @Column(name = "image_3d_url", nullable = false)
    private String image3dUrl = "";

    @Column(name = "max_supply", nullable = false)
    private Integer maxSupply;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<NftToken> tokens = new ArrayList<>();

    // 현재 발행된 수량 조회
    public int getCurrentSupply() {
        return tokens.size();
    }

    // 추가 발행 가능 여부 확인
    public boolean canMintMore() {
        return getCurrentSupply() < maxSupply;
    }

}