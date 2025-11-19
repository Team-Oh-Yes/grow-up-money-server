package com.ohyes.GrowUpMoney.domain.nft.repository;

import com.ohyes.GrowUpMoney.domain.nft.entity.ThemeReward;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeRewardRepository {

    // 특정 사용자의 모든 보상 조회
    @Query("SELECT tr FROM ThemeReward tr WHERE tr.member.username = :username ORDER BY tr.completedAt DESC")
    List<ThemeReward> findByUsername(@Param("username") String username);

    // 특정 테마의 모든 보상 조회
    List<ThemeReward> findByThemeId(Long themeId);

    // 특정 사용자가 특정 테마를 완료한 횟수
    @Query("SELECT COUNT(tr) FROM ThemeReward tr WHERE tr.member.username = :username AND tr.theme.id = :themeId")
    long countByUsernameAndThemeId(@Param("username") String username, @Param("themeId") Long themeId);

    // 특정 사용자의 특정 테마 보상 내역
    @Query("SELECT tr FROM ThemeReward tr WHERE tr.member.username = :username AND tr.theme.id = :themeId ORDER BY tr.completedAt DESC")
    List<ThemeReward> findByUsernameAndThemeId(@Param("username") String username, @Param("themeId") Long themeId);

    // 특정 컬렉션이 보상으로 선택된 횟수
    long countByCollectionId(Long collectionId);

    // 특정 사용자가 특정 컬렉션을 보상으로 받은 횟수
    @Query("SELECT COUNT(tr) FROM ThemeReward tr WHERE tr.member.username = :username AND tr.collection.id = :collectionId")
    long countByUsernameAndCollectionId(@Param("username") String username, @Param("collectionId") Long collectionId);

    // 사용자의 보상을 테마, 컬렉션 정보와 함께 조회
    @Query("SELECT tr FROM ThemeReward tr " +
            "LEFT JOIN FETCH tr.theme " +
            "LEFT JOIN FETCH tr.collection " +
            "WHERE tr.member.username = :username " +
            "ORDER BY tr.completedAt DESC")
    List<ThemeReward> findByUsernameWithDetails(@Param("username") String username);

    // 특정 토큰에 연결된 보상 조회
    @Query("SELECT tr FROM ThemeReward tr WHERE tr.token.id = :tokenId")
    ThemeReward findByTokenId(@Param("tokenId") Long tokenId);

    // 가장 인기있는 컬렉션 TOP N (보상으로 가장 많이 선택된 순)
    @Query("SELECT tr.collection.id, COUNT(tr) as cnt FROM ThemeReward tr GROUP BY tr.collection.id ORDER BY cnt DESC")
    List<Object[]> findMostPopularCollections();
}
