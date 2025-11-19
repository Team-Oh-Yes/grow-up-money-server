package com.ohyes.GrowUpMoney.domain.nft.repository;

import com.ohyes.GrowUpMoney.domain.nft.entity.NftToken;
import com.ohyes.GrowUpMoney.domain.nft.entity.enums.TokenType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NftTokenRepository {
    // 특정 사용자가 소유한 모든 NFT 조회
    @Query("SELECT t FROM NftToken t WHERE t.owner.username = :username")
    List<NftToken> findByOwnerUsername(@Param("username") String username);

    // 특정 사용자가 소유한 특정 타입의 NFT 조회
    @Query("SELECT t FROM NftToken t WHERE t.owner.username = :username AND t.tokenType = :tokenType")
    List<NftToken> findByOwnerUsernameAndTokenType(@Param("username") String username,
                                                   @Param("tokenType") TokenType tokenType);

    // 특정 컬렉션의 모든 토큰 조회
    List<NftToken> findByCollectionId(Long collectionId);

    // 특정 컬렉션의 특정 타입 토큰 조회
    List<NftToken> findByCollectionIdAndTokenType(Long collectionId, TokenType tokenType);

    // 특정 컬렉션의 특정 타입 토큰 개수
    long countByCollectionIdAndTokenType(Long collectionId, TokenType tokenType);

    // 판매 중인 NFT 조회
    @Query("SELECT t FROM NftToken t WHERE t.isOnSale = true AND t.tokenType = 'TRADEABLE'")
    List<NftToken> findAllOnSale();

    // 특정 사용자의 판매 중인 NFT 조회
    @Query("SELECT t FROM NftToken t WHERE t.owner.username = :username AND t.isOnSale = true")
    List<NftToken> findByOwnerUsernameAndIsOnSale(@Param("username") String username);

    // 토큰과 컬렉션 정보를 함께 조회
    @Query("SELECT t FROM NftToken t LEFT JOIN FETCH t.collection WHERE t.id = :tokenId")
    Optional<NftToken> findByIdWithCollection(@Param("tokenId") Long tokenId);

    // 사용자의 NFT를 컬렉션과 함께 조회
    @Query("SELECT t FROM NftToken t LEFT JOIN FETCH t.collection WHERE t.owner.username = :username")
    List<NftToken> findByOwnerUsernameWithCollection(@Param("username") String username);

    // 특정 컬렉션의 다음 시리얼 번호 조회
    @Query("SELECT COALESCE(MAX(t.serialNo), 0) FROM NftToken t WHERE t.collection.id = :collectionId AND t.tokenType = 'TRADEABLE'")
    Integer findMaxSerialNoByCollectionId(@Param("collectionId") Long collectionId);

    // 사용자가 특정 컬렉션의 NFT를 보유하고 있는지 확인
    @Query("SELECT COUNT(t) > 0 FROM NftToken t WHERE t.owner.username = :username AND t.collection.id = :collectionId")
    boolean existsByOwnerUsernameAndCollectionId(@Param("username") String username,
                                                 @Param("collectionId") Long collectionId);

}
