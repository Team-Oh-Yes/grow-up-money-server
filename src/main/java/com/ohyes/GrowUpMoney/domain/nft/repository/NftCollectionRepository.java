package com.ohyes.GrowUpMoney.domain.nft.repository;

import com.ohyes.GrowUpMoney.domain.nft.entity.NftCollection;
import com.ohyes.GrowUpMoney.domain.nft.entity.enums.Rarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NftCollectionRepository extends JpaRepository<NftCollection, Long> {

    // 특정 테마의 모든 컬렉션 조회
    List<NftCollection> findByThemeId(Long themeId);

    // 희귀도별 컬렉션 조회
    List<NftCollection> findByRarity(Rarity rarity);

    // 이름으로 컬렉션 검색
    @Query("SELECT nc FROM NftCollection nc WHERE nc.name LIKE CONCAT('%', :keyword, '%')")
    List<NftCollection> searchByKeyword(@Param("keyword") String keyword);

    // 특정 테마의 컬렉션을 생성일 기준으로 정렬
    List<NftCollection> findByThemeIdOrderByCreatedAtAsc(Long themeId);

    // 컬렉션과 토큰들을 함께 조회 (N+1 문제 해결)
    @Query("SELECT DISTINCT c FROM NftCollection c LEFT JOIN FETCH c.tokens WHERE c.id = :collectionId")
    NftCollection findByIdWithTokens(@Param("collectionId") Long collectionId);

    // 모든 컬렉션을 테마와 함께 조회
    @Query("SELECT DISTINCT c FROM NftCollection c LEFT JOIN FETCH c.theme ORDER BY c.theme.id, c.id")
    List<NftCollection> findAllWithTheme();

    // 뽑기 가능한 NFT 조회 (재고가 남아있는 것만)
    @Query("SELECT nc FROM NftCollection nc " +
            "WHERE (SELECT COUNT(nt) FROM NftToken nt WHERE nt.collection = nc) < nc.maxSupply " +
            "ORDER BY nc.maxSupply ASC")
    List<NftCollection> findAvailableForGacha();

    // 특정 테마에 속한 컬렉션 개수
    long countByThemeId(Long themeId);

    // 컬렉션명 중복 확인
    boolean existsByName(String name);
}