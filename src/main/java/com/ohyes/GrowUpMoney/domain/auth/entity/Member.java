package com.ohyes.GrowUpMoney.domain.auth.entity;

import com.ohyes.GrowUpMoney.domain.auth.enums.SuspensionType;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftToken;
import com.ohyes.GrowUpMoney.domain.nft.entity.ThemeReward;
import com.ohyes.GrowUpMoney.domain.nft.entity.Trade;
import com.ohyes.GrowUpMoney.domain.auth.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tb_member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = true)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    // ========================================================================
    //프로필 관련
    @Column(length = 250)
    private String introduction;  // 자기소개

    @Column(length = 500)
    private String profileImageUrl;  // 프로필 이미지 URL

    @Column
    private Long favoriteNftId;  // 대표 에쁘띠 ID

    // ========================================================================
    //포인트,하트 관련

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer dailyEarnedPoints = 0;  // 오늘 획득한 포인트

    @Column
    private LocalDateTime lastDailyPointReset;  // 마지막 일일 포인트 리셋 시간

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer pointBalance = 0;  // NFT 거래 가능한 포인트

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer boundPoint = 0;  // 귀속 포인트 (퀴즈 보상, 거래 불가)

    @Column(nullable = false, columnDefinition = "int default 5")
    private Integer hearts = 5;  // 하루 5개, 틀리면 -1, 0되면 50포인트로 구매

    @Column
    private LocalDateTime lastHeartReset;  // 마지막 하트 리셋 시간 (매일 0시)

    @Column(length = 20)
    private String tier;  // 배지 등급 (누적 포인트 기반)

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer totalEarnedPoints = 0;  // 누적 획득 포인트

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) default 'ACTIVE'")
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column
    private LocalDateTime suspendedUntil;

    @Enumerated(EnumType.STRING)
    private SuspensionType suspensionType;

    @Column
    private String suspensionReason;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) default 'user'")
    private String role = "ROLE_USER";

    @CreationTimestamp
    private LocalDateTime createdAt;

    @CreationTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<NftToken> ownedTokens = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private List<Trade> salesHistory = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    private List<Trade> purchaseHistory = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ThemeReward> rewards = new ArrayList<>();

    // ========================================================================

    // 자기소개 수정
    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    // 프로필 이미지 수정
    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // 대표 NFT 수정
    public void updateFavoriteNft(Long favoriteNftId) {
        this.favoriteNftId = favoriteNftId;
    }

    // 일일 획득 포인트 추가
    public void addDailyPoints(Integer amount) {
        this.dailyEarnedPoints += amount;
    }

    // 일일 획득 포인트 리셋 확인
    public boolean needsDailyPointReset() {
        if (this.lastDailyPointReset == null) {
            return true;
        }
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        return this.lastDailyPointReset.isBefore(today);
    }

    // 일일 획득 포인트 리셋
    public void resetDailyPoints() {
        this.dailyEarnedPoints = 0;
        this.lastDailyPointReset = LocalDateTime.now();
    }

    // ========================================================================

    // 계정 정지
    public void suspend(int days, String reason, SuspensionType type) {
        this.status = MemberStatus.SUSPENDED;
        this.suspensionReason = reason;
        this.suspensionType = type;

        if (days == -1) {
            this.suspendedUntil = LocalDateTime.of(9000, 12, 31, 23, 59, 59);
        } else {
            this.suspendedUntil = LocalDateTime.now().plusDays(days);
        }
    }

    // 귀속 포인트 추가 (퀴즈 보상)
    public void addBoundPoint(Integer amount) {
        this.boundPoint += amount;
        this.totalEarnedPoints += amount;
        this.dailyEarnedPoints += amount;  // 일일 포인트에도 추가
    }

    // 거래 가능 포인트 추가 (충전, 거래 수익 등)
    public void addPoint(Integer amount) {
        this.pointBalance += amount;
    }

    // 포인트 차감
    public void deductPoint(Integer amount) {
        if (this.pointBalance < amount) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }
        this.pointBalance -= amount;
    }

    // 귀속 포인트 → 거래 가능 포인트 전환
    public void convertBoundPointToPoint(Integer amount) {
        if (this.boundPoint < amount) {
            throw new IllegalStateException("귀속 포인트가 부족합니다.");
        }
        this.boundPoint -= amount;
        this.pointBalance += amount;
    }

    public void unsuspend() {
        this.status = MemberStatus.ACTIVE;
        this.suspendedUntil = null;
        this.suspensionReason = null;
    }

    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
        this.suspendedUntil = null;
        this.suspensionReason = "회원 탈퇴";
    }

    public boolean isSuspensionExpired() {
        if (this.status != MemberStatus.SUSPENDED) {
            return false;
        }
        return this.suspendedUntil != null &&
                LocalDateTime.now().isAfter(this.suspendedUntil);
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    // 하트 차감 (퀴즈 오답)
    public void deductHeart() {
        if (this.hearts > 0) {
            this.hearts -= 1;
        }
    }

    // 하트 구매 (50 포인트)
    public void purchaseHeart() {
        if (this.pointBalance < 50) {
            throw new IllegalStateException("포인트가 부족합니다. 필요: 50");
        }
        this.pointBalance -= 50;
        this.hearts += 1;
    }

    // 하트 리셋 확인 (매일 0시)
    public boolean needsHeartReset() {
        if (this.lastHeartReset == null) {
            return true;
        }
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        return this.lastHeartReset.isBefore(today);
    }

    // 하트 리셋 (매일 5개로 초기화)
    public void resetHearts() {
        this.hearts = 5;
        this.lastHeartReset = LocalDateTime.now();
    }

    // 배지 등급 업데이트 (누적 포인트)
    public void updateTier() {
        if (this.totalEarnedPoints >= 100000) {
            this.tier = "DIAMOND";
        } else if (this.totalEarnedPoints >= 50000) {
            this.tier = "PLATINUM";
        } else if (this.totalEarnedPoints >= 20000) {
            this.tier = "GOLD";
        } else if (this.totalEarnedPoints >= 10000) {
            this.tier = "SILVER";
        } else if (this.totalEarnedPoints >= 5000) {
            this.tier = "BRONZE";
        } else {
            this.tier = "BEGINNER";
        }
    }
}