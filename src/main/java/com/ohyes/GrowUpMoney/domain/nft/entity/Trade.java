package com.ohyes.GrowUpMoney.domain.nft.entity;

import com.ohyes.GrowUpMoney.domain.nft.entity.enums.TradeStatus;
import com.ohyes.GrowUpMoney.domain.user.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_trade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false)
    private NftToken token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_username", nullable = false)
    private Member seller;

    // 판매 중이면 null로 표시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_username")
    private Member buyer;

    @Column(nullable = false)
    private Integer price;

    // 수수료 5% 떼감
    @Column(nullable = false)
    private Integer fee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TradeStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    // 거래 완료 처리
    public void completeTrade(Member buyer) {
        if (this.status != TradeStatus.LISTING) {
            throw new IllegalStateException("판매 중인 상품만 구매할 수 있습니다.");
        }
        this.buyer = buyer;
        this.status = TradeStatus.SOLD;
        this.soldAt = LocalDateTime.now();
        this.token.setNotOnSale();
        this.token.transferTo(buyer);
    }

    // 거래 취소
    public void cancelTrade() {
        if (this.status != TradeStatus.LISTING) {
            throw new IllegalStateException("판매 중인 상품만 취소할 수 있습니다.");
        }
        this.status = TradeStatus.CANCELLED;
        this.token.setNotOnSale();
    }

    // 가격 수정
    public void updatePrice(Integer newPrice) {
        if (this.status != TradeStatus.LISTING) {
            throw new IllegalStateException("판매 중인 상품만 가격을 수정할 수 있습니다.");
        }
        this.price = newPrice;
        this.fee = calculateFee(newPrice);
    }

    // 수수료 계산 5%
    private Integer calculateFee(Integer price) {
        return (int) Math.round(price * 0.05);
    }

    // 판매자가 받을 금액 계산
    public Integer getSellerProceeds() {
        return price - fee;
    }

}
