package com.ohyes.GrowUpMoney.domain.roadmap.repository;

import com.ohyes.GrowUpMoney.domain.roadmap.entity.ProgressStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgressRepository, Long> {

//    사용자의 특정 단원 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.lesson.id = :lessonId")
    Optional<UserLessonProgressRepository> findByUsernameAndLessonId(@Param("username") String username,
                                                                     @Param("lessonId") Long lessonId);

//    사용자의 모든 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username")
    List<UserLessonProgressRepository> findByUsername(@Param("username") String username);

//    특정 테마 대한 사용자의 모든 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "JOIN ulp.lesson l " +
            "WHERE ulp.member.username = :username AND l.theme.id = :themeId " +
            "ORDER BY l.orderIndex ASC")
    List<UserLessonProgressRepository> findByUsernameAndThemeId(@Param("username") String username,
                                                                @Param("themeId") Long themeId);

//    사용자의 특정 상태의 진행 상황 조회
    @Query("SELECT ulp FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.status = :status")
    List<UserLessonProgressRepository> findByUsernameAndStatus(@Param("username") String username,
                                                               @Param("status") ProgressStatus status);

//    사용자의 완료된 단원 개수
    @Query("SELECT COUNT(ulp) FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.status = :status")
    long countByUsernameAndStatus(@Param("username") String username,
                                  @Param("status") ProgressStatus status);

//    특정 테마에서 사용자의 완료된 단원 개수
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

//    사용자의 완료된 단원 개수 조회
    @Query("SELECT COUNT(ulp) FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.status = 'COMPLETED'")
    Long countCompletedLessonsByUsername(@Param("username") String username);

//    특정 테마에서 사용자의 완료된 단원 개수 조회
    @Query("SELECT COUNT(ulp) FROM UserLessonProgress ulp " +
            "JOIN ulp.lesson l " +
            "WHERE ulp.member.username = :username " +
            "AND l.theme.id = :themeId " +
            "AND ulp.status = 'COMPLETED'")
    Long countCompletedLessonsByUsernameAndThemeId(
            @Param("username") String username,
            @Param("themeId") Long themeId
    );

//    사용자의 총 정답 수
    @Query("SELECT SUM(ulp.correctCount) FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username")
    Integer getTotalCorrectCount(@Param("username") String username);

//    사용자의 총 시도 수
    @Query("SELECT SUM(ulp.totalAttempted) FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username")
    Integer getTotalAttemptedCount(@Param("username") String username);

//    진행 상황이 존재하는지 확인
    @Query("SELECT CASE WHEN COUNT(ulp) > 0 THEN true ELSE false END " +
            "FROM UserLessonProgress ulp " +
            "WHERE ulp.member.username = :username AND ulp.lesson.id = :lessonId")
    boolean existsByUsernameAndLessonId(@Param("username") String username,
                                        @Param("lessonId") Long lessonId);

}