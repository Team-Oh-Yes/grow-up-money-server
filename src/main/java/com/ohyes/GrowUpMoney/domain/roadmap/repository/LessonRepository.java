package com.ohyes.GrowUpMoney.domain.roadmap.repository;

import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // 특정 테마의 모든 단원을 순서대로 조회
    List<Lesson> findByThemeIdOrderByOrderIndexAsc(Long themeId);

    // 전체 단원 개수 조회
    @Query("SELECT COUNT(l) FROM Lesson l")
    Long countAllLessons();

    // 특정 테마의 단원 개수 조회
    long countByThemeId(Long themeId);

    // 특정 테마와 순서로 단원 조회
    Optional<Lesson> findByThemeIdAndOrderIndex(Long themeId, Integer orderIndex);

    // 단원 제목으로 검색
    List<Lesson> findByTitleContaining(String title);

    // 특정 테마의 첫 번째 단원 조회
    @Query("SELECT l FROM Lesson l WHERE l.theme.id = :themeId ORDER BY l.orderIndex ASC LIMIT 1")
    Optional<Lesson> findFirstLessonByThemeId(@Param("themeId") Long themeId);

    // 특정 테마의 마지막 단원 조회
    @Query("SELECT l FROM Lesson l WHERE l.theme.id = :themeId ORDER BY l.orderIndex DESC LIMIT 1")
    Optional<Lesson> findLastLessonByThemeId(@Param("themeId") Long themeId);

    // 다음 단원 조회
    @Query("SELECT l FROM Lesson l WHERE l.theme.id = :themeId AND l.orderIndex > :currentOrderIndex ORDER BY l.orderIndex ASC LIMIT 1")
    Optional<Lesson> findNextLesson(@Param("themeId") Long themeId, @Param("currentOrderIndex") Integer currentOrderIndex);

    // 이전 단원 조회
    @Query("SELECT l FROM Lesson l WHERE l.theme.id = :themeId AND l.orderIndex < :currentOrderIndex ORDER BY l.orderIndex DESC LIMIT 1")
    Optional<Lesson> findPreviousLesson(@Param("themeId") Long themeId, @Param("currentOrderIndex") Integer currentOrderIndex);

}
