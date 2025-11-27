package com.ohyes.GrowUpMoney.domain.nft.entity;

import com.ohyes.GrowUpMoney.domain.nft.entity.enums.TokenType;
import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
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
@Table(name = "tb_nft_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NftToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private NftCollection collection;

    @Column(name = "serial_no")
    private Integer serialNo;  // 거래용 NFT만 사용 (1/100, 2/100...)

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 20)
    private TokenType tokenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_username", nullable = false)
    private Member owner;

    @CreationTimestamp
    @Column(name = "minted_at", nullable = false, updatable = false)
    private LocalDateTime mintedAt;

    @Column(name = "is_on_sale", nullable = false)
    private Boolean isOnSale = false;

    @OneToMany(mappedBy = "token", cascade = CascadeType.ALL)
    private List<Trade> trades = new ArrayList<>();

    @OneToOne(mappedBy = "token")
    private ThemeReward themeReward;

    // 거래용 NFT인지 확인
    public boolean isTradeable() {
        return tokenType == TokenType.TRADEABLE;
    }

    // 도감용 NFT인지 확인
    public boolean isCollection() {
        return tokenType == TokenType.COLLECTION;
    }

    // 소유자 변경
    public void transferTo(Member newOwner) {
        this.owner = newOwner;
    }

    // 판매 상태 변경
    public void setOnSale() {
        if (!isTradeable()) {
            throw new IllegalStateException("도감용 NFT는 판매할 수 없습니다.");
        }
        this.isOnSale = true;
    }

    public void setNotOnSale() {
        this.isOnSale = false;
    }

}