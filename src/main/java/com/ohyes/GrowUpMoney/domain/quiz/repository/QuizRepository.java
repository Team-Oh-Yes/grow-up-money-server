package com.ohyes.GrowUpMoney.domain.quiz.repository;

import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import com.ohyes.GrowUpMoney.domain.quiz.entity.enums.Difficulty;
import com.ohyes.GrowUpMoney.domain.quiz.entity.enums.QuestionType;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Question, Long> {

    // Lesson으로 Question 조회
    List<Question> findByLesson(Lesson lesson);

    // Lesson ID로 Question 조회
    List<Question> findByLessonId(Long lessonId);

    // Lesson ID와 orderIndex로 정렬된 Question 조회
    List<Question> findByLessonIdOrderByOrderIndexAsc(Long lessonId);

    // 난이도로 Question 조회
    List<Question> findByDifficulty(Difficulty difficulty);

    // Question 타입으로 조회
    List<Question> findByType(QuestionType type);

    // 프리미엄 여부로 조회
    List<Question> findByIsPremium(Boolean isPremium);

    // Lesson과 isPremium으로 조회
    List<Question> findByLessonIdAndIsPremium(Long lessonId, Boolean isPremium);

    // Lesson ID와 난이도로 조회
    List<Question> findByLessonIdAndDifficulty(Long lessonId, Difficulty difficulty);

    // 특정 Lesson의 Question 개수
    long countByLessonId(Long lessonId);

    // 특정 Lesson의 프리미엄 Question 개수
    long countByLessonIdAndIsPremium(Long lessonId, Boolean isPremium);
}