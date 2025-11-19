package com.ohyes.GrowUpMoney.domain.nft.repository;

import com.ohyes.GrowUpMoney.domain.nft.entity.Trade;
import com.ohyes.GrowUpMoney.domain.nft.entity.enums.TradeStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    // 특정 상태의 거래 조회
    List<Trade> findByStatus(TradeStatus status);

    // 판매 중인 거래 조회
    List<Trade> findByStatusOrderByCreatedAtDesc(TradeStatus status);

    // 특정 사용자가 판매한 거래 내역
    @Query("SELECT t FROM Trade t WHERE t.seller.username = :username ORDER BY t.createdAt DESC")
    List<Trade> findBySellerUsername(@Param("username") String username);

    // 특정 사용자가 구매한 거래 내역
    @Query("SELECT t FROM Trade t WHERE t.buyer.username = :username ORDER BY t.soldAt DESC")
    List<Trade> findByBuyerUsername(@Param("username") String username);

    // 특정 토큰의 거래 내역
    List<Trade> findByTokenIdOrderByCreatedAtDesc(Long tokenId);

    // 특정 토큰의 진행 중인 거래
    Optional<Trade> findByTokenIdAndStatus(Long tokenId, TradeStatus status);

    // 특정 기간 동안의 거래 내역
    @Query("SELECT t FROM Trade t WHERE t.status = :status AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Trade> findByStatusAndCreatedAtBetween(@Param("status") TradeStatus status,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    // 특정 컬렉션의 거래 내역
    @Query("SELECT t FROM Trade t WHERE t.token.collection.id = :collectionId AND t.status = :status")
    List<Trade> findByCollectionIdAndStatus(@Param("collectionId") Long collectionId,
                                            @Param("status") TradeStatus status);

    // 특정 컬렉션의 평균 거래가
    @Query("SELECT AVG(t.price) FROM Trade t WHERE t.token.collection.id = :collectionId AND t.status = 'SOLD'")
    Double findAveragePriceByCollectionId(@Param("collectionId") Long collectionId);

    // 특정 컬렉션의 최근 N개 거래 평균가
    @Query("SELECT AVG(t.price) FROM (SELECT t2.price FROM Trade t2 WHERE t2.token.collection.id = :collectionId AND t2.status = 'SOLD' ORDER BY t2.soldAt DESC LIMIT :limit) t")
    Double findRecentAveragePriceByCollectionId(@Param("collectionId") Long collectionId,
                                                @Param("limit") int limit);

    // 특정 컬렉션의 최저가
    @Query("SELECT MIN(t.price) FROM Trade t WHERE t.token.collection.id = :collectionId AND t.status = 'LISTING'")
    Integer findMinPriceByCollectionId(@Param("collectionId") Long collectionId);

    // 특정 컬렉션의 최고가
    @Query("SELECT MAX(t.price) FROM Trade t WHERE t.token.collection.id = :collectionId AND t.status = 'LISTING'")
    Integer findMaxPriceByCollectionId(@Param("collectionId") Long collectionId);

    // 토큰이 현재 판매 중인지 확인
    @Query("SELECT COUNT(t) > 0 FROM Trade t WHERE t.token.id = :tokenId AND t.status = 'LISTING'")
    boolean existsActiveTradeByTokenId(@Param("tokenId") Long tokenId);

}
