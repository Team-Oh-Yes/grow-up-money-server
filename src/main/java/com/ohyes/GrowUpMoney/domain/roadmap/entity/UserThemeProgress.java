package com.ohyes.GrowUpMoney.domain.roadmap.entity;

import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_user_theme_progress",
uniqueConstraints = @UniqueConstraint(columnNames = {"username", "theme_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserThemeProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProgressStatus status;

    @Column(name = "correct_count", nullable = false)
    private Integer correctCount;

    @Column(name = "total_attempted", nullable = false)
    private Integer totalAttempted;

    @UpdateTimestamp
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public UserThemeProgress(Member member, Theme theme, ProgressStatus status) {
        this.member = member;
        this.theme = theme;
        this.status = status != null ? status : ProgressStatus.NOT_STARTED;
        this.correctCount = 0;
        this.totalAttempted = 0;
    }

    public void startLesson() {
        if (this.status == ProgressStatus.NOT_STARTED) {
            this.status = ProgressStatus.IN_PROGRESS;
        }
    }

}
