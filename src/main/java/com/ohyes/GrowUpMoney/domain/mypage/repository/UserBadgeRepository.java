package com.ohyes.GrowUpMoney.domain.mypage.repository;

import com.ohyes.GrowUpMoney.domain.mypage.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.member.id = :memberId ORDER BY ub.badge.badgeLevel ASC")
    List<UserBadge> findByMemberIdWithBadge(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(ub) > 0 FROM UserBadge ub WHERE ub.member.id = :memberId AND ub.badge.id = :badgeId")
    boolean existsByMemberIdAndBadgeId(@Param("memberId") Long memberId, @Param("badgeId") Long badgeId);

    @Query("SELECT ub.badge.id FROM UserBadge ub WHERE ub.member.id = :memberId")
    List<Long> findBadgeIdsByMemberId(@Param("memberId") Long memberId);
}