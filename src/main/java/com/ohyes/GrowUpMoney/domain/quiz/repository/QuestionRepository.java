package com.ohyes.GrowUpMoney.domain.quiz.repository;

import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import com.ohyes.GrowUpMoney.domain.quiz.enums.Difficulty;
import com.ohyes.GrowUpMoney.domain.quiz.enums.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.lesson.id = :lessonId ORDER BY q.orderIndex ASC")
    List<Question> findByLessonIdOrderByOrderIndex(@Param("lessonId") Long lessonId);

    @Query("SELECT q FROM Question q WHERE q.lesson.id = :lessonId AND q.isPremium = false ORDER BY q.orderIndex ASC")
    List<Question> findNormalQuestionsByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT q FROM Question q WHERE q.lesson.id = :lessonId AND q.isPremium = true ORDER BY q.orderIndex ASC")
    List<Question> findPremiumQuestionsByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT q FROM Question q WHERE q.lesson.id = :lessonId AND q.difficulty = :difficulty ORDER BY q.orderIndex ASC")
    List<Question> findByLessonIdAndDifficulty(@Param("lessonId") Long lessonId, @Param("difficulty") Difficulty difficulty);

    @Query("SELECT q FROM Question q WHERE q.lesson.id = :lessonId AND q.type = :type ORDER BY q.orderIndex ASC")
    List<Question> findByLessonIdAndType(@Param("lessonId") Long lessonId, @Param("type") QuestionType type);

    long countByLessonId(Long lessonId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.lesson.id = :lessonId AND q.isPremium = false")
    long countNormalByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.lesson.id = :lessonId AND q.isPremium = true")
    long countPremiumByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT q FROM Question q WHERE q.lesson.theme.id = :themeId ORDER BY q.lesson.orderIndex, q.orderIndex ASC")
    List<Question> findByThemeId(@Param("themeId") Long themeId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.lesson.theme.id = :themeId")
    long countByThemeId(@Param("themeId") Long themeId);

    @Query("SELECT q FROM Question q JOIN FETCH q.lesson l JOIN FETCH l.theme WHERE q.id = :questionId")
    Question findByIdWithLessonAndTheme(@Param("questionId") Long questionId);

    @Query("SELECT q FROM Question q JOIN FETCH q.lesson WHERE q.lesson.id = :lessonId ORDER BY q.orderIndex ASC")
    List<Question> findByLessonIdWithLesson(@Param("lessonId") Long lessonId);
}