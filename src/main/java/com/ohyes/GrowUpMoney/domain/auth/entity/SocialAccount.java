package com.ohyes.GrowUpMoney.domain.auth.entity;

import com.ohyes.GrowUpMoney.domain.auth.enums.SocialProvider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "tb_oauth_account")
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider provider;

    @Column(nullable = false)
    private String providerId;  // 소셜에서 주는 고유 ID

    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
