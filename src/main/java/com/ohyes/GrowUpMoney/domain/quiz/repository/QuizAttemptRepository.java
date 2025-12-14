package com.ohyes.GrowUpMoney.domain.quiz.repository;

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

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.member.username = :username AND qa.question.id = :questionId ORDER BY qa.createdAt DESC")
    List<QuizAttempt> findByUsernameAndQuestionId(@Param("username") String username, @Param("questionId") Long questionId);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.member.username = :username AND qa.question.id = :questionId ORDER BY qa.createdAt DESC LIMIT 1")
    Optional<QuizAttempt> findLatestByUsernameAndQuestionId(@Param("username") String username, @Param("questionId") Long questionId);

    @Query("SELECT COUNT(qa) > 0 FROM QuizAttempt qa WHERE qa.member.username = :username AND qa.question.id = :questionId AND qa.isCorrect = true")
    boolean existsCorrectAttempt(@Param("username") String username, @Param("questionId") Long questionId);

    @Query("SELECT COUNT(DISTINCT qa.question.id) FROM QuizAttempt qa JOIN qa.question q WHERE qa.member.username = :username AND q.lesson.id = :lessonId AND qa.isCorrect = true")
    long countCorrectByUsernameAndLessonId(@Param("username") String username, @Param("lessonId") Long lessonId);

    @Query("SELECT COUNT(DISTINCT qa.question.id) FROM QuizAttempt qa JOIN qa.question q WHERE qa.member.username = :username AND q.lesson.theme.id = :themeId AND qa.isCorrect = true")
    long countCorrectByUsernameAndThemeId(@Param("username") String username, @Param("themeId") Long themeId);

    @Query("SELECT DISTINCT qa.question.id FROM QuizAttempt qa JOIN qa.question q WHERE qa.member.username = :username AND q.lesson.id = :lessonId AND qa.isCorrect = false AND qa.question.id NOT IN (SELECT qa2.question.id FROM QuizAttempt qa2 WHERE qa2.member.username = :username AND qa2.isCorrect = true)")
    List<Long> findWrongQuestionIdsByLessonId(@Param("username") String username, @Param("lessonId") Long lessonId);

    @Query("SELECT DISTINCT qa.question.id FROM QuizAttempt qa WHERE qa.member.username = :username AND qa.isCorrect = false AND qa.question.id NOT IN (SELECT qa2.question.id FROM QuizAttempt qa2 WHERE qa2.member.username = :username AND qa2.isCorrect = true)")
    List<Long> findAllWrongQuestionIds(@Param("username") String username);

    @Query("SELECT COALESCE(SUM(qa.awardedPoints), 0) FROM QuizAttempt qa WHERE qa.member.username = :username AND qa.createdAt >= :startOfDay")
    int sumTodayAwardedPoints(@Param("username") String username, @Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT COALESCE(SUM(qa.awardedPoints), 0) FROM QuizAttempt qa JOIN qa.question q WHERE qa.member.username = :username AND q.lesson.id = :lessonId")
    int sumAwardedPointsByLessonId(@Param("username") String username, @Param("lessonId") Long lessonId);

    @Query("SELECT COALESCE(SUM(qa.awardedPoints), 0) FROM QuizAttempt qa JOIN qa.question q WHERE qa.member.username = :username AND q.lesson.theme.id = :themeId")
    int sumAwardedPointsByThemeId(@Param("username") String username, @Param("themeId") Long themeId);

    @Query("SELECT DISTINCT q.lesson.theme.id FROM QuizAttempt qa JOIN qa.question q WHERE qa.member.username = :username AND qa.createdAt >= :startOfDay")
    List<Long> findTodayPlayedThemeIds(@Param("username") String username, @Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT COUNT(DISTINCT q.lesson.theme.id) FROM QuizAttempt qa JOIN qa.question q WHERE qa.member.username = :username AND qa.createdAt >= :startOfDay")
    long countTodayPlayedThemes(@Param("username") String username, @Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT qa FROM QuizAttempt qa JOIN FETCH qa.question q JOIN FETCH q.lesson WHERE qa.member.username = :username AND q.lesson.id = :lessonId ORDER BY qa.createdAt DESC")
    List<QuizAttempt> findByUsernameAndLessonIdWithQuestion(@Param("username") String username, @Param("lessonId") Long lessonId);
}