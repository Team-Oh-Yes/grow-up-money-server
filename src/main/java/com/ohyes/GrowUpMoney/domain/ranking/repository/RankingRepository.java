package com.ohyes.GrowUpMoney.domain.ranking.repository;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Member, Long> {

    // 전체 랭킹 조회 (totalEarnedPoints 기준 내림차순, ACTIVE 회원만, 페이징)
    Page<Member> findAllByStatusOrderByTotalEarnedPointsDesc(MemberStatus status, Pageable pageable);

    // 사용자명으로 검색 (부분 일치, ACTIVE 회원만)
    List<Member> findByUsernameContainingAndStatusOrderByTotalEarnedPointsDesc(String username, MemberStatus status);

    // 특정 사용자보다 포인트가 높은 사용자 수 (순위 계산용, ACTIVE 회원만)
    @Query("SELECT COUNT(m) FROM Member m WHERE m.totalEarnedPoints > :totalEarnedPoints AND m.status = 'ACTIVE'")
    Long countUsersWithHigherPoints(@Param("totalEarnedPoints") Integer totalEarnedPoints);

    // 특정 순위 범위의 사용자 조회 (ACTIVE 회원만)
    @Query("SELECT m FROM Member m WHERE m.status = 'ACTIVE' ORDER BY m.totalEarnedPoints DESC")
    List<Member> findTopUsers(Pageable pageable);

    // 사용자 아이디로 조회
    Optional<Member> findById(Long userId);

    // 사용자명으로 조회
    Optional<Member> findByUsername(String username);

    // 전체 사용자 수 (ACTIVE 회원만)
    @Query("SELECT COUNT(m) FROM Member m WHERE m.status = 'ACTIVE'")
    Long countTotalActiveUsers();
}