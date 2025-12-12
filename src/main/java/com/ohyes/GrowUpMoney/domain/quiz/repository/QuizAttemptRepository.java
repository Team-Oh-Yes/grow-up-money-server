
package com.ohyes.GrowUpMoney.domain.quiz.repository;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import com.ohyes.GrowUpMoney.domain.quiz.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // 특정 회원의 모든 시도 조회
    List<QuizAttempt> findByMember(Member member);

    // 특정 회원 ID의 모든 시도 조회
    List<QuizAttempt> findByMemberId(Long memberId);

    // 특정 회원의 특정 문제 시도 조회
    List<QuizAttempt> findByMemberAndQuestion(Member member, Question question);

    // 특정 회원의 특정 문제 시도 조회 (ID 사용)
    List<QuizAttempt> findByMemberIdAndQuestionId(Long memberId, Long questionId);

    // 특정 회원의 가장 최근 시도 조회
    Optional<QuizAttempt> findTopByMemberIdOrderByAttemptedAtDesc(Long memberId);

    // 특정 회원의 특정 문제에 대한 가장 최근 시도
    Optional<QuizAttempt> findTopByMemberIdAndQuestionIdOrderByAttemptedAtDesc(
            Long memberId, Long questionId);

    // 특정 회원의 정답/오답 시도만 조회
    List<QuizAttempt> findByMemberIdAndIsCorrect(Long memberId, Boolean isCorrect);

    // 특정 회원의 총 획득 포인트 계산
    @Query("SELECT COALESCE(SUM(qa.pointsEarned), 0) FROM QuizAttempt qa WHERE qa.member.id = :memberId")
    Integer getTotalPointsByMemberId(@Param("memberId") Long memberId);

    // 특정 회원의 정답률 계산
    @Query("SELECT CAST(COUNT(CASE WHEN qa.isCorrect = true THEN 1 END) AS double) / COUNT(*) " +
            "FROM QuizAttempt qa WHERE qa.member.id = :memberId")
    Double getCorrectRateByMemberId(@Param("memberId") Long memberId);

    // 특정 기간 동안의 시도 조회
    List<QuizAttempt> findByMemberIdAndAttemptedAtBetween(
            Long memberId, LocalDateTime startDate, LocalDateTime endDate);

    // 특정 문제에 대한 모든 시도 조회
    List<QuizAttempt> findByQuestionId(Long questionId);

    // 특정 회원이 특정 문제를 정답 처리했는지 확인
    boolean existsByMemberIdAndQuestionIdAndIsCorrect(
            Long memberId, Long questionId, Boolean isCorrect);

    // 특정 회원의 시도 횟수
    long countByMemberId(Long memberId);

    // 특정 회원의 특정 문제 시도 횟수
    long countByMemberIdAndQuestionId(Long memberId, Long questionId);

    // 최근 N개의 시도 조회
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.member.id = :memberId " +
            "ORDER BY qa.attemptedAt DESC")
    List<QuizAttempt> findRecentAttempts(@Param("memberId") Long memberId);}
