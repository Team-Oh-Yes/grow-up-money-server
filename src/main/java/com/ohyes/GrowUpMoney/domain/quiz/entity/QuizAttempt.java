package com.ohyes.GrowUpMoney.domain.quiz.entity;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_quiz_attempt")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "question", nullable = false)
    private Question question;

    @Column(name = "user_answer")
    private String userAnswer;

    @Column(name="is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name="points_earned", nullable = false)
    private Integer pointsEarned;

    @CreationTimestamp
    @Column(name = "attempted_at", updatable = false)
    private LocalDateTime attemptedAt;

    @PrePersist
    protected void onCreate() {
        this.attemptedAt = LocalDateTime.now();
    }

    // 정답 체크 및 포인트 계산
    public void checkAndRecordAnswer() {
        this.isCorrect = question.checkAnswer(this.userAnswer);
        this.pointsEarned = this.isCorrect ? question.getPointReward() : 0;
    }

    // 유저 답변 업데이트
    public void updateUserAnswer(String newAnswer) {
        this.userAnswer = newAnswer;
        checkAndRecordAnswer();
    }

}
