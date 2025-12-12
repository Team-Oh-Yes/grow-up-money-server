package com.ohyes.GrowUpMoney.domain.quiz.repository;

import com.ohyes.GrowUpMoney.domain.quiz.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // 특정 회원 ID의 모든 시도
    List<QuizAttempt> findByMemberId(Long memberId);

    // 특정 회원의 특정 문제 시도
    List<QuizAttempt> findByMemberIdAndQuestionId(Long memberId, Long questionId);

    // 전체 누적 포인트
    @Query("SELECT COALESCE(SUM(qa.pointsEarned), 0) FROM QuizAttempt qa WHERE qa.member.id = :memberId")
    Integer getTotalPointsByMemberId(@Param("memberId") Long memberId);

    // 전체 정답률
    @Query("""
           SELECT 
              CAST(COUNT(CASE WHEN qa.isCorrect = true THEN 1 END) AS double) 
              / COUNT(*)
           FROM QuizAttempt qa
           WHERE qa.member.id = :memberId
           """)
    Double getCorrectRateByMemberId(@Param("memberId") Long memberId);


    // 단원 내에서 맞힌 문제 수 (진행률 계산용)
    @Query("""
            SELECT COUNT(qa) FROM QuizAttempt qa
            WHERE qa.member.id = :memberId
            AND qa.question.lesson.id = :lessonId
            AND qa.isCorrect = true
           """)
    int countCorrectAnswersInLesson(@Param("memberId") Long memberId,
                                    @Param("lessonId") Long lessonId);

}
