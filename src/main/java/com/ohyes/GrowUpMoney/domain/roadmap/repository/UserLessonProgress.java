package com.ohyes.GrowUpMoney.domain.roadmap.repository;

import com.ohyes.GrowUpMoney.domain.roadmap.entity.ProgressStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonProgress extends JpaRepository<UserLessonProgress, Long> {

//    사용자의 특정 단원 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.lesson.id = :lessonId")
    Optional<UserLessonProgress> findByUsernameAndLessonId(@Param("username") String username,
                                                           @Param("lessonId") Long lessonId);

//    사용자의 모든 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username")
    List<UserLessonProgress> findByUsername(@Param("username") String username);

//    특정 테마 대한 사용자의 모든 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "JOIN ulp.lesson l " +
            "WHERE ulp.member.username = :username AND l.theme.id = :themeId " +
            "ORDER BY l.orderIndex ASC")
    List<UserLessonProgress> findByUsernameAndThemeId(@Param("username") String username,
                                                      @Param("themeId") Long themeId);

//    사용자의 특정 상태의 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.status = :status")
    List<UserLessonProgress> findByUsernameAndStatus(@Param("username") String username,
                                                     @Param("status") ProgressStatus status);

//    사용자의 완료된 레슨 개수
    @Query("SELECT COUNT(ulp) FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.status = :status")
    long countByUsernameAndStatus(@Param("username") String username,
                                  @Param("status") ProgressStatus status);

//    특정 테마에서 사용자의 완료된 레슨 개수
    @Query("SELECT COUNT(ulp) FROM UserLessonProgress ulp " +
            "JOIN ulp.lesson l " +
            "WHERE ulp.member.username = :username " +
            "AND l.theme.id = :themeId " +
            "AND ulp.status = :status")
    long countByUsernameAndThemeIdAndStatus(
            @Param("username") String username,
            @Param("themeId") Long themeId,
            @Param("status") ProgressStatus status
    );

//    사용자의 전체 진행률 계산



}
