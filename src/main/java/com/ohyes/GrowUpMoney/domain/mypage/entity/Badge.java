package com.ohyes.GrowUpMoney.domain.mypage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String badgeName;

    @Column(nullable = false, length = 200)
    private String badgeDescription;

    @Column(nullable = false, length = 500)
    private String badgeImageUrl;

    @Column(nullable = false)
    private Long requiredPoints;

    @Column(nullable = false)
    @Builder.Default
    private Integer badgeLevel = 1;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 배지 획득 가능 여부 확인
    public boolean canAcquire(Integer userTotalPoints) {
        return userTotalPoints >= this.requiredPoints;
    }
}