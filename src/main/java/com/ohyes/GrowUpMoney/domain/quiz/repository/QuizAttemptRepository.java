package com.ohyes.GrowUpMoney.domain.quiz.repository;

import com.ohyes.GrowUpMoney.domain.quiz.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // 회원의 틀린 문제 조회 (Fetch Join)
    @Query("SELECT qa FROM QuizAttempt qa " +
            "JOIN FETCH qa.question q " +
            "JOIN FETCH q.lesson " +
            "WHERE qa.member.id = :memberId " +
            "AND qa.isCorrect = false " +
            "ORDER BY qa.attemptedAt DESC")
    List<QuizAttempt> findWrongAttemptsByMemberId(@Param("memberId") Long memberId);

    // 회원의 특정 Lesson 틀린 문제 조회
    @Query("SELECT qa FROM QuizAttempt qa " +
            "JOIN FETCH qa.question q " +
            "WHERE qa.member.id = :memberId " +
            "AND q.lesson.id = :lessonId " +
            "AND qa.isCorrect = false " +
            "ORDER BY qa.attemptedAt DESC")
    List<QuizAttempt> findWrongAttemptsByMemberAndLesson(
            @Param("memberId") Long memberId,
            @Param("lessonId") Long lessonId
    );

    // 회원의 Lesson별 맞춘 문제 수
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa " +
            "WHERE qa.member.id = :memberId " +
            "AND qa.question.lesson.id = :lessonId " +
            "AND qa.isCorrect = true")
    long countCorrectByMemberAndLesson(
            @Param("memberId") Long memberId,
            @Param("lessonId") Long lessonId
    );

}