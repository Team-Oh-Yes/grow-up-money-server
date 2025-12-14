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
@Table(name = "tb_user_lesson_progress",
uniqueConstraints = @UniqueConstraint(columnNames = {"username", "lesson_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username",referencedColumnName = "username", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

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
    public UserLessonProgress(Member member, Lesson lesson, ProgressStatus status) {
        this.member = member;
        this.lesson = lesson;
        this.status = status != null ? status : ProgressStatus.NOT_STARTED;
        this.correctCount = 0;
        this.totalAttempted = 0;
    }

    public void startLesson() {
        if (this.status == ProgressStatus.NOT_STARTED) {
            this.status = ProgressStatus.IN_PROGRESS;
        }
    }

    public void completeLesson() {
        this.status = ProgressStatus.COMPLETED;
    }

    public void updateQuizResult(boolean isCorrect) {
        this.totalAttempted++;
        if (isCorrect) {
            this.correctCount++;
        }
    }

    public double getAccuracy() {
        if (totalAttempted == 0) {
            return 0.0;
        }
        return (double) correctCount / totalAttempted * 100;
    }

    public String getUsername() {
        return member.getUsername();
    }

}
