package com.ohyes.GrowUpMoney.domain.mypage.repository;

import com.ohyes.GrowUpMoney.domain.mypage.entity.FavoriteNft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteNftRepository extends JpaRepository<FavoriteNft, Long> {

    @Query("SELECT f FROM FavoriteNft f JOIN FETCH f.nftToken nt JOIN FETCH nt.collection WHERE f.member.id = :memberId ORDER BY f.createdAt DESC")
    List<FavoriteNft> findByMemberIdWithNftToken(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(f) > 0 FROM FavoriteNft f WHERE f.member.id = :memberId AND f.nftToken.id = :tokenId")
    boolean existsByMemberIdAndTokenId(@Param("memberId") Long memberId, @Param("tokenId") Long tokenId);

    @Query("SELECT f FROM FavoriteNft f WHERE f.member.id = :memberId AND f.nftToken.id = :tokenId")
    Optional<FavoriteNft> findByMemberIdAndTokenId(@Param("memberId") Long memberId, @Param("tokenId") Long tokenId);

    @Query("SELECT COUNT(f) FROM FavoriteNft f WHERE f.member.id = :memberId")
    Long countByMemberId(@Param("memberId") Long memberId);
}