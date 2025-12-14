package com.ohyes.GrowUpMoney.domain.quiz.entity;

import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 퀴즈 풀이 기록 남기는 테이블
@Entity
@Table(name = "tb_quiz_attempt", indexes = {
        @Index(name = "idx_quiz_attempt_username", columnList = "username"),
        @Index(name = "idx_quiz_attempt_question", columnList = "question_id"),
        @Index(name = "idx_quiz_attempt_created", columnList = "created_at")
})
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizAttempt {
    // 풀이 기록 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_attempt_id")
    private Long id;

    // 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "username" , nullable = false)
    private Member member;

    // 문제 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // 사용자 답
    @Column(name = "user_answer")
    private String userAnswer;

    // 채점결과
    @Column(name="is_correct", nullable = false)
    private Boolean isCorrect;

    // 받은 포인트
    @Column(name = "awarded_points", nullable = false)
    private Integer awardedPoints;

    // 시도 횟수
    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    // 생성 시간
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.attemptCount == null) {
            this.attemptCount = 1;
        }
        if (this.awardedPoints == null) {
            this.awardedPoints = 0;
        }
    }

    public void grade(boolean isCorrect, int awardedPoints) {
        this.isCorrect = isCorrect;
        this.awardedPoints = awardedPoints;
    }

    public void incrementAttemptCount() {
        this.attemptCount++;
    }

    public void markAsCorrect(int awardedPoints) {
        this.isCorrect = true;
        this.awardedPoints = awardedPoints;
        this.attemptCount++;
    }

}
