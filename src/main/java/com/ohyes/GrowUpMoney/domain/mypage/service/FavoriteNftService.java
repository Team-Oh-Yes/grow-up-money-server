package com.ohyes.GrowUpMoney.domain.mypage.service;

import com.ohyes.GrowUpMoney.domain.mypage.entity.FavoriteNft;
import com.ohyes.GrowUpMoney.domain.mypage.repository.FavoriteNftRepository;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftToken;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftTokenRepository;
import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FavoriteNftService {

    private final FavoriteNftRepository favoriteNftRepository;
    private final NftTokenRepository nftTokenRepository;
    private final MemberRepository memberRepository;

    // 즐겨찾기 추가
    @Transactional
    public void addFavorite(String username, Long tokenId) {
        log.info("즐겨찾기 추가: username={}, tokenId={}", username, tokenId);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        NftToken nftToken = nftTokenRepository.findById(tokenId)
                .orElseThrow(() -> new IllegalArgumentException("NFT 토큰을 찾을 수 없습니다: " + tokenId));

        // 이미 즐겨찾기에 있는지 확인
        if (favoriteNftRepository.existsByMemberIdAndTokenId(member.getId(), tokenId)) {
            throw new IllegalStateException("이미 즐겨찾기에 추가된 NFT입니다.");
        }

        FavoriteNft favoriteNft = FavoriteNft.builder()
                .member(member)
                .nftToken(nftToken)
                .build();

        favoriteNftRepository.save(favoriteNft);
        log.info("즐겨찾기 추가 완료: favoriteId={}", favoriteNft.getId());
    }

    // 즐겨찾기 제거
    @Transactional
    public void removeFavorite(String username, Long tokenId) {
        log.info("즐겨찾기 제거: username={}, tokenId={}", username, tokenId);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        FavoriteNft favoriteNft = favoriteNftRepository.findByMemberIdAndTokenId(member.getId(), tokenId)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기를 찾을 수 없습니다."));

        favoriteNftRepository.delete(favoriteNft);
        log.info("즐겨찾기 제거 완료");
    }

    // 즐겨찾기 목록 조회
    public List<FavoriteNft> getFavoriteList(Long memberId) {
        return favoriteNftRepository.findByMemberIdWithNftToken(memberId);
    }

    // 즐겨찾기 개수 조회
    public Long getFavoriteCount(Long memberId) {
        return favoriteNftRepository.countByMemberId(memberId);
    }

    // 즐겨찾기 여부 확인
    public boolean isFavorite(Long memberId, Long tokenId) {
        return favoriteNftRepository.existsByMemberIdAndTokenId(memberId, tokenId);
    }
}