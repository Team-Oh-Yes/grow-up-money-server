package com.ohyes.GrowUpMoney.domain.quiz.repository;

import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import com.ohyes.GrowUpMoney.domain.quiz.enums.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Lesson별 문제 조회 (순서대로, Fetch Join)
    @Query("SELECT q FROM Question q " +
            "JOIN FETCH q.lesson " +
            "WHERE q.lesson.id = :lessonId " +
            "ORDER BY q.orderIndex ASC")
    List<Question> findByLessonIdWithLesson(@Param("lessonId") Long lessonId);

    // Lesson + Difficulty별 문제 조회
    @Query("SELECT q FROM Question q " +
            "JOIN FETCH q.lesson " +
            "WHERE q.lesson.id = :lessonId AND q.difficulty = :difficulty " +
            "ORDER BY q.orderIndex ASC")
    List<Question> findByLessonIdAndDifficultyWithLesson(
            @Param("lessonId") Long lessonId,
            @Param("difficulty") Difficulty difficulty
    );

    // Lesson의 총 문제 수
    long countByLessonId(Long lessonId);

    // Lesson의 Difficulty별 문제 수
    long countByLessonIdAndDifficulty(Long lessonId, Difficulty difficulty);
}