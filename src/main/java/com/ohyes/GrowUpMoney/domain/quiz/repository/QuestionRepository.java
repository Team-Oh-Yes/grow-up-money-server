package com.ohyes.GrowUpMoney.domain.quiz.repository;

import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // 단원(Lesson) 안의 문제를 순서(orderIndex)대로 조회
    List<Question> findByLesson_IdOrderByOrderIndex(Long lessonId);

    /*
     Fetch Join 버전 — N+1 방지용
     (Lesson 정보를 함께 로딩하고 싶을 때)
     */
    @Query("""
           SELECT q FROM Question q
           JOIN FETCH q.lesson l
           WHERE l.id = :lessonId
           ORDER BY q.orderIndex
           """)
    List<Question> findAllByLessonIdFetchJoin(@Param("lessonId") Long lessonId);


     // 특정 단원 안의 문제 개수 (진행률 계산용)
    @Query("""
           SELECT COUNT(q) FROM Question q
           WHERE q.lesson.id = :lessonId
           """)
    int countQuestionsInLesson(@Param("lessonId") Long lessonId);
}
