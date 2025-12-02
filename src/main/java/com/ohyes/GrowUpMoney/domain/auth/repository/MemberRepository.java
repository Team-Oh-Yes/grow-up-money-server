package com.ohyes.GrowUpMoney.domain.auth.repository;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    // 사용자명으로 조회
    Optional<Member> findByUsername(String username);

    // 사용자명 존재 여부
    boolean existsByUsername(String username);

    // 이메일로 조회
    Optional<Member> findByEmail(String email);

    // 이메일 존재 여부
    boolean existsByEmail(String email);

    @Override
    Page<Member> findAll(Pageable pageable);

    // 총 포인트 기준 상위 N명 조회 (랭킹)
    @Query("SELECT m FROM Member m ORDER BY m.totalEarnedPoints DESC")
    List<Member> findTopByOrderByTotalEarnedPointsDesc();

    // 특정 사용자의 랭킹 조회
    @Query("SELECT COUNT(m) + 1 FROM Member m WHERE m.totalEarnedPoints > :totalEarnedPoints")
    Long findRankByTotalEarnedPoints(@Param("totalEarnedPoints") Integer totalEarnedPoints);

    // 일일 포인트 리셋이 필요한 사용자 조회
    @Query("SELECT m FROM Member m WHERE m.lastDailyPointReset IS NULL OR m.lastDailyPointReset < :resetDate")
    List<Member> findMembersNeedingDailyReset(@Param("resetDate") LocalDateTime resetDate);

    // 하트 리셋이 필요한 사용자 조회
    @Query("SELECT m FROM Member m WHERE m.lastHeartReset IS NULL OR m.lastHeartReset < :resetDate")
    List<Member> findMembersNeedingHeartReset(@Param("resetDate") LocalDateTime resetDate);

}
