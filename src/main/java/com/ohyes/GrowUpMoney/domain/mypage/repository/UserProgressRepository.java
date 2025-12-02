package com.ohyes.GrowUpMoney.domain.mypage.repository;

import com.ohyes.GrowUpMoney.domain.mypage.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    @Query("SELECT up FROM UserProgress up WHERE up.member.id = :memberId ORDER BY up.themeId, up.unitId")
    List<UserProgress> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT up FROM UserProgress up WHERE up.member.id = :memberId AND up.themeId = :themeId ORDER BY up.unitId")
    List<UserProgress> findByMemberIdAndThemeId(@Param("memberId") Long memberId, @Param("themeId") Long themeId);

    @Query("SELECT up FROM UserProgress up WHERE up.member.id = :memberId AND up.themeId = :themeId AND up.unitId = :unitId")
    Optional<UserProgress> findByMemberIdAndThemeIdAndUnitId(@Param("memberId") Long memberId, @Param("themeId") Long themeId, @Param("unitId") Long unitId);

    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.member.id = :memberId AND up.isCompleted = true")
    Long countCompletedByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.member.id = :memberId AND up.themeId = :themeId AND up.isCompleted = true")
    Long countCompletedByMemberIdAndThemeId(@Param("memberId") Long memberId, @Param("themeId") Long themeId);
}