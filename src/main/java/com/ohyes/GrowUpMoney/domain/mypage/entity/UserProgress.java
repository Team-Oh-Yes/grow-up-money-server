package com.ohyes.GrowUpMoney.domain.mypage.entity;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "theme_id", "unit_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "theme_id", nullable = false)
    private Long themeId;

    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @Column
    private Integer quizScore;

    @Column
    private Integer attemptCount;

    @Column
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 단원 완료 처리
    public void complete(Integer quizScore) {
        this.isCompleted = true;
        this.quizScore = quizScore;
        this.completedAt = LocalDateTime.now();
    }

    // 시도 횟수 증가
    public void incrementAttemptCount() {
        this.attemptCount = (this.attemptCount == null ? 0 : this.attemptCount) + 1;
    }
}