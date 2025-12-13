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

// 퀴즈 풀이 기록 남기는 테이블
@Entity
@Table(name = "tb_quiz_attempt")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizAttempt {
    // 풀이 기록 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_attempt_id", unique = true, nullable = false)
    private Long id;

    // 회원
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 문제 아이디
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // 사용자 답
    @Column(name = "user_answer")
    private String userAnswer;

    // 채점결과
    @Column(name="is_correct", nullable = false)
    private Boolean isCorrect;

    // 재시도 여부
    @Column(name = "is_retry", nullable = false)
    private Boolean isRetry = false;

    // 지급 포인트
    @Column(name="points_earned", nullable = false)
    private Integer pointsEarned;

    // 풀이 시간
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
