package com.ohyes.GrowUpMoney.domain.mypage.repository;

import com.ohyes.GrowUpMoney.domain.mypage.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    // 레벨 순으로 모든 배지 조회
    @Query("SELECT b FROM Badge b ORDER BY b.badgeLevel ASC, b.requiredPoints ASC")
    List<Badge> findAllOrderByLevel();

    // 특정 포인트 이하의 배지 조회
    @Query("SELECT b FROM Badge b WHERE b.requiredPoints <= :points ORDER BY b.requiredPoints ASC")
    List<Badge> findAcquirableBadges(@Param("points") Integer points);

    // 배지명으로 조회
    Badge findByBadgeName(String badgeName);
}