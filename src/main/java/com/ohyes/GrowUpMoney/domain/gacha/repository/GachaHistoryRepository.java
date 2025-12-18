package com.ohyes.GrowUpMoney.domain.gacha.repository;

import com.ohyes.GrowUpMoney.domain.gacha.entity.GachaHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GachaHistoryRepository extends JpaRepository<GachaHistory, Long> {

    // 사용자별 뽑기 이력 조회 (페이징)
    @Query("SELECT gh FROM GachaHistory gh WHERE gh.member.username = :username ORDER BY gh.createdAt DESC")
    Page<GachaHistory> findByUsername(@Param("username") String username, Pageable pageable);

    // 사용자별 뽑기 이력 조회 (전체)
    @Query("SELECT gh FROM GachaHistory gh WHERE gh.member.username = :username ORDER BY gh.createdAt DESC")
    List<GachaHistory> findAllByUsername(@Param("username") String username);

    // 사용자별 최근 뽑기 이력 조회 (개수 제한)
    @Query("SELECT gh FROM GachaHistory gh WHERE gh.member.username = :username ORDER BY gh.createdAt DESC")
    List<GachaHistory> findRecentByUsername(@Param("username") String username,
                                            Pageable pageable);

    // 특정 기간 뽑기 이력 조회
    @Query("SELECT gh FROM GachaHistory gh WHERE gh.member.username = :username " +
            "AND gh.createdAt BETWEEN :startDate AND :endDate ORDER BY gh.createdAt DESC")
    List<GachaHistory> findByUsernameAndDateRange(@Param("username") String username,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    // 사용자별 뽑기 총 횟수
    @Query("SELECT COUNT(gh) FROM GachaHistory gh WHERE gh.member.username = :username")
    Long countByUsername(@Param("username") String username);
}