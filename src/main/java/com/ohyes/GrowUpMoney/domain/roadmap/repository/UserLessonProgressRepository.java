package com.ohyes.GrowUpMoney.domain.roadmap.repository;

import com.ohyes.GrowUpMoney.domain.roadmap.entity.ProgressStatus;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {

    // 사용자의 특정 단원 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.lesson.id = :lessonId")
    Optional<UserLessonProgress> findByUsernameAndLessonId(@Param("username") String username,
                                                            @Param("lessonId") Long lessonId);

    // 사용자의 모든 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username")
    List<UserLessonProgress> findByUsername(@Param("username") String username);

    // 특정 테마에 대한 사용자의 모든 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "JOIN ulp.lesson l " +
            "WHERE ulp.member.username = :username AND l.theme.id = :themeId " +
            "ORDER BY l.orderIndex ASC")
    List<UserLessonProgress> findByUsernameAndThemeId(@Param("username") String username,
                                                       @Param("themeId") Long themeId);

    // 사용자의 특정 상태의 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.status = :status")
    List<UserLessonProgress> findByUsernameAndStatus(@Param("username") String username,
                                                      @Param("status") ProgressStatus status);

    // 사용자의 특정 상태의 단원 개수
    @Query("SELECT COUNT(ulp) FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.status = :status")
    long countByUsernameAndStatus(@Param("username") String username,
                                   @Param("status") ProgressStatus status);

    // 특정 테마에서 사용자의 특정 상태의 단원 개수
    @Query("SELECT COUNT(ulp) FROM UserLessonProgress ulp " +
            "JOIN ulp.lesson l " +
            "WHERE ulp.member.username = :username " +
            "AND l.theme.id = :themeId " +
            "AND ulp.status = :status")
    long countByUsernameAndThemeIdAndStatus(@Param("username") String username,
                                             @Param("themeId") Long themeId,
                                             @Param("status") ProgressStatus status);

    // 사용자의 총 정답 수
    @Query("SELECT COALESCE(SUM(ulp.correctCount), 0) FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username")
    long getTotalCorrectCount(@Param("username") String username);

    // 사용자의 총 시도 수
    @Query("SELECT COALESCE(SUM(ulp.totalAttempted), 0) FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username")
    long getTotalAttemptedCount(@Param("username") String username);

    // 진행 상황이 존재하는지 확인
    @Query("SELECT CASE WHEN COUNT(ulp) > 0 THEN true ELSE false END " +
            "FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.lesson.id = :lessonId")
    boolean existsByUsernameAndLessonId(@Param("username") String username,
                                         @Param("lessonId") Long lessonId);

}
