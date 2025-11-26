package com.ohyes.GrowUpMoney.domain.nft.service;

import com.ohyes.GrowUpMoney.domain.nft.dto.request.ThemeRewardSelectRequest;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.NftCollectionResponse;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.NftTokenResponse;
import com.ohyes.GrowUpMoney.domain.nft.dto.response.ThemeRewardResponse;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftCollection;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftToken;
import com.ohyes.GrowUpMoney.domain.nft.entity.ThemeReward;
import com.ohyes.GrowUpMoney.domain.nft.exception.NftException;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftCollectionRepository;
import com.ohyes.GrowUpMoney.domain.nft.repository.ThemeRewardRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Theme;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.ThemeRepository;
import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ThemeRewardService {

    private final ThemeRewardRepository themeRewardRepository;
    private final NftCollectionRepository nftCollectionRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final NftTokenService nftTokenService;

    // 테마 완료 보상 선택 가능한 NFT 목록
    public List<NftCollectionResponse> getAvailableRewards(Long themeId) {
        log.info("테마 보상 NFT 목록 조회: themeId={}", themeId);

        // 테마 존재 확인
        if (!themeRepository.existsById(themeId)) {
            throw new IllegalArgumentException("테마를 찾을 수 없습니다. ID: " + themeId);
        }

        return nftCollectionRepository.findByThemeId(themeId).stream()
                .map(NftCollectionResponse::from)
                .collect(Collectors.toList());
    }

    // 테마 완료 보상 선택
    @Transactional
    public ThemeRewardResponse selectReward(String username, ThemeRewardSelectRequest request) {
        log.info("테마 보상 선택 시작: username={}, themeId={}, collectionId={}",
                username, request.getThemeId(), request.getCollectionId());

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new IllegalArgumentException("테마를 찾을 수 없습니다. ID: " + request.getThemeId()));

        NftCollection collection = nftCollectionRepository.findById(request.getCollectionId())
                .orElseThrow(() -> new NftException.NftCollectionNotFoundException(request.getCollectionId()));

        // 컬렉션이 해당 테마에 속하는지 확인
        if (!collection.getTheme().getId().equals(request.getThemeId())) {
            throw new NftException.CollectionNotInThemeException(request.getCollectionId(), request.getThemeId());
        }

        // TODO: 테마 완료 여부 확인 (RoadmapService와 연동 필요)
        // if (!isThemeCompleted(username, themeId)) {
        //     throw new NftException.ThemeNotCompletedException(themeId);
        // }

        // 도감용 NFT 발급
        NftTokenResponse tokenResponse = nftTokenService.mintCollectionNft(
                request.getCollectionId(), username);

        NftToken token = new NftToken();
        token.setId(tokenResponse.getTokenId());

        // 보상 기록 저장
        ThemeReward reward = new ThemeReward();
        reward.setMember(member);
        reward.setTheme(theme);
        reward.setCollection(collection);
        reward.setToken(token);

        ThemeReward saved = themeRewardRepository.save(reward);
        log.info("테마 보상 선택 완료: rewardId={}, tokenId={}",
                saved.getId(), tokenResponse.getTokenId());

        return ThemeRewardResponse.from(saved);
    }

    // 내 보상 내역
    public List<ThemeRewardResponse> getMyRewards(String username) {
        log.info("내 보상 내역 조회: username={}", username);

        return themeRewardRepository.findByUsernameWithDetails(username).stream()
                .map(ThemeRewardResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 테마의 보상 횟수 조회
    public long getThemeRewardCount(String username, Long themeId) {
        log.info("테마 보상 횟수 조회: username={}, themeId={}", username, themeId);

        return themeRewardRepository.countByUsernameAndThemeId(username, themeId);
    }

    // 특정 컬렉션을 보상으로 받은 횟수
    public long getCollectionRewardCount(String username, Long collectionId) {
        log.info("컬렉션 보상 횟수 조회: username={}, collectionId={}", username, collectionId);

        return themeRewardRepository.countByUsernameAndCollectionId(username, collectionId);
    }

    // 인기 컬렉션 TOP 10
    public List<Object[]> getPopularCollections() {
        log.info("인기 컬렉션 조회");

        return themeRewardRepository.findMostPopularCollections();
    }

}