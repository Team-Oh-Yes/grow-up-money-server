package com.ohyes.GrowUpMoney.domain.mypage.service;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.mypage.dto.request.ProfileUpdateRequest;
import com.ohyes.GrowUpMoney.domain.mypage.dto.response.*;
import com.ohyes.GrowUpMoney.domain.mypage.entity.Badge;
import com.ohyes.GrowUpMoney.domain.mypage.entity.FavoriteNft;
import com.ohyes.GrowUpMoney.domain.mypage.entity.UserProgress;
import com.ohyes.GrowUpMoney.domain.mypage.exception.MyPageException;
import com.ohyes.GrowUpMoney.domain.mypage.repository.BadgeRepository;
import com.ohyes.GrowUpMoney.domain.nft.entity.NftToken;
import com.ohyes.GrowUpMoney.domain.nft.entity.Trade;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftCollectionRepository;
import com.ohyes.GrowUpMoney.domain.nft.repository.NftTokenRepository;
import com.ohyes.GrowUpMoney.domain.nft.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MyPageService {

    private final MemberRepository memberRepository;
    private final NftTokenRepository nftTokenRepository;
    private final NftCollectionRepository nftCollectionRepository;
    private final BadgeRepository badgeRepository;
    private final TradeRepository tradeRepository;
    private final PasswordEncoder passwordEncoder;
    private final BadgeService badgeService;
    private final FavoriteNftService favoriteNftService;
    private final ProgressService progressService;

    private static final int TOTAL_THEME_COUNT = 10;
    private static final int UNITS_PER_THEME = 5;

    // 프로필 조회
    public MyProfileResponse getMyProfile(String username) {
        log.info("프로필 조회: username={}", username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MyPageException.ProfileNotFoundException(username));

        // 대표 NFT 정보 조회
        String favoriteNftName = null;
        String favoriteNftImageUrl = null;

        if (member.getFavoriteNftId() != null) {
            NftToken favoriteNft = nftTokenRepository.findByIdWithCollection(member.getFavoriteNftId())
                    .orElse(null);

            if (favoriteNft != null) {
                favoriteNftName = favoriteNft.getCollection().getName();
                favoriteNftImageUrl = favoriteNft.getCollection().getImageUrl();
            }
        }

        return MyProfileResponse.builder()
                .userId(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .profileImageUrl(member.getProfileImageUrl())
                .introduction(member.getIntroduction())
                .favoriteNftId(member.getFavoriteNftId())
                .favoriteNftName(favoriteNftName)
                .favoriteNftImageUrl(favoriteNftImageUrl)
                .tier(member.getTier())
                .build();
    }

    // 프로필 수정
    @Transactional
    public MyProfileResponse updateMyProfile(String username, ProfileUpdateRequest request) {
        log.info("프로필 수정: username={}", username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MyPageException.ProfileNotFoundException(username));

        // 이메일 업데이트 (중복 체크)
        if (request.getEmail() != null && !request.getEmail().equals(member.getEmail())) {
            if (memberRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getEmail());
            }
            member.updateEmail(request.getEmail());
        }

        // 자기소개 업데이트
        if (request.getIntroduction() != null) {
            member.updateIntroduction(request.getIntroduction());
        }

        // 프로필 이미지 업데이트
        if (request.getProfileImageUrl() != null) {
            member.updateProfileImage(request.getProfileImageUrl());
        }

        // 대표 NFT 설정
        if (request.getFavoriteNftId() != null) {
            NftToken nft = nftTokenRepository.findById(request.getFavoriteNftId())
                    .orElseThrow(() -> new MyPageException.NotOwnedNftException(request.getFavoriteNftId()));

            if (!nft.getOwner().getId().equals(member.getId())) {
                throw new MyPageException.NotOwnedNftException(request.getFavoriteNftId());
            }

            member.updateFavoriteNft(request.getFavoriteNftId());
        }

        // 비밀번호 변경
        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
                throw new MyPageException.PasswordMismatchException();
            }

            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            member.updatePassword(encodedPassword);
        }

        memberRepository.save(member);

        return getMyProfile(username);
    }

    // 배지 조회 (실제 데이터)
    public BadgeResponse getMyBadges(String username) {
        log.info("배지 조회: username={}", username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MyPageException.ProfileNotFoundException(username));

        // 모든 배지 조회
        List<Badge> allBadges = badgeService.getAllBadges();

        // 사용자가 획득한 배지 ID 목록
        List<Long> acquiredBadgeIds = badgeService.getUserBadgeIds(member.getId());

        // 배지 응답 생성
        List<BadgeResponse.Badge> badgeList = allBadges.stream()
                .map(badge -> BadgeResponse.Badge.builder()
                        .badgeId(badge.getId())
                        .badgeName(badge.getBadgeName())
                        .badgeDescription(badge.getBadgeDescription())
                        .badgeImageUrl(badge.getBadgeImageUrl())
                        .requiredPoints(badge.getRequiredPoints())
                        .isAcquired(acquiredBadgeIds.contains(badge.getId()))
                        .build())
                .collect(Collectors.toList());

        return BadgeResponse.builder()
                .totalPoints((long) member.getTotalEarnedPoints())
                .badges(badgeList)
                .build();
    }

    // 도감 조회 (실제 데이터)
    public EncyclopediaResponse getMyEncyclopedia(String username) {
        log.info("도감 조회: username={}", username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MyPageException.ProfileNotFoundException(username));

        // 전체 NFT 컬렉션 조회
        List<EncyclopediaResponse.NftEncyclopedia> nftList = nftCollectionRepository.findAll().stream()
                .map(collection -> {
                    // 사용자가 해당 컬렉션의 NFT를 보유하고 있는지 확인
                    List<NftToken> ownedTokens = nftTokenRepository
                            .findByOwnerIdAndCollectionId(member.getId(), collection.getId());

                    return EncyclopediaResponse.NftEncyclopedia.builder()
                            .collectionId(collection.getId())
                            .nftName(collection.getName())
                            .nftImageUrl(collection.getImageUrl())
                            .rarity(collection.getRarity().name())
                            .isAcquired(!ownedTokens.isEmpty())
                            .ownedCount(ownedTokens.size())
                            .build();
                })
                .collect(Collectors.toList());

        long acquiredCount = nftList.stream()
                .filter(EncyclopediaResponse.NftEncyclopedia::getIsAcquired)
                .count();

        return EncyclopediaResponse.builder()
                .totalNftCount(nftList.size())
                .acquiredNftCount((int) acquiredCount)
                .nftList(nftList)
                .build();
    }

    // 학습 진척도 조회 (실제 데이터)
    public ProgressResponse getMyProgress(String username) {
        log.info("학습 진척도 조회: username={}", username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MyPageException.ProfileNotFoundException(username));

        // 전체 진척도 조회
        List<UserProgress> allProgress = progressService.getUserProgress(member.getId());

        // 테마별로 그룹화
        Map<Long, List<UserProgress>> progressByTheme = allProgress.stream()
                .collect(Collectors.groupingBy(UserProgress::getThemeId));

        // 테마별 진행률 생성
        List<ProgressResponse.ThemeProgress> themeProgressList = new ArrayList<>();

        for (long themeId = 1; themeId <= TOTAL_THEME_COUNT; themeId++) {
            List<UserProgress> themeProgress = progressByTheme.getOrDefault(themeId, new ArrayList<>());

            // 단원별 진행률 생성
            List<ProgressResponse.UnitProgress> unitProgressList = new ArrayList<>();
            for (long unitId = 1; unitId <= UNITS_PER_THEME; unitId++) {
                final long finalUnitId = unitId;
                Optional<UserProgress> unitProgress = themeProgress.stream()
                        .filter(up -> up.getUnitId().equals(finalUnitId))
                        .findFirst();

                unitProgressList.add(ProgressResponse.UnitProgress.builder()
                        .unitId(unitId)
                        .unitName("단원 " + unitId)
                        .isCompleted(unitProgress.isPresent() && unitProgress.get().getIsCompleted())
                        .quizScore(unitProgress.map(UserProgress::getQuizScore).orElse(null))
                        .build());
            }

            long completedUnits = unitProgressList.stream()
                    .filter(ProgressResponse.UnitProgress::getIsCompleted)
                    .count();

            themeProgressList.add(ProgressResponse.ThemeProgress.builder()
                    .themeId(themeId)
                    .themeName("테마 " + themeId)
                    .totalUnitCount(UNITS_PER_THEME)
                    .completedUnitCount((int) completedUnits)
                    .themeProgress((completedUnits * 100.0) / UNITS_PER_THEME)
                    .unitProgressList(unitProgressList)
                    .build());
        }

        long completedThemes = progressService.getCompletedThemeCount(member.getId());
        double overallProgress = progressService.calculateOverallProgress(member.getId());

        return ProgressResponse.builder()
                .totalThemeCount(TOTAL_THEME_COUNT)
                .completedThemeCount((int) completedThemes)
                .overallProgress(overallProgress)
                .themeProgressList(themeProgressList)
                .build();
    }

    // 즐겨찾기 NFT 목록 조회 (실제 데이터)
    public FavoriteNftResponse getMyFavorites(String username) {
        log.info("즐겨찾기 NFT 조회: username={}", username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MyPageException.ProfileNotFoundException(username));

        List<FavoriteNft> favorites = favoriteNftService.getFavoriteList(member.getId());

        List<FavoriteNftResponse.FavoriteNft> favoriteNftList = favorites.stream()
                .map(fav -> {
                    NftToken token = fav.getNftToken();

                    // 판매 등록 여부 확인
                    Optional<Trade> activeTrade = tradeRepository.findActiveTradeByTokenId(token.getId());

                    return FavoriteNftResponse.FavoriteNft.builder()
                            .tokenId(token.getId())
                            .collectionId(token.getCollection().getId())
                            .nftName(token.getCollection().getName())
                            .nftImageUrl(token.getCollection().getImageUrl())
                            .serialNumber(token.getSerialNumber())
                            .rarity(token.getCollection().getRarity().name())
                            .isOnSale(activeTrade.isPresent())
                            .currentPrice(activeTrade.map(Trade::getPrice).map(Long::valueOf).orElse(null))
                            .build();
                })
                .collect(Collectors.toList());

        return FavoriteNftResponse.builder()
                .totalFavoriteCount(favoriteNftList.size())
                .favoriteNftList(favoriteNftList)
                .build();
    }

    // 보유 포인트 조회
    public PointResponse getMyPoints(String username) {
        log.info("보유 포인트 조회: username={}", username);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MyPageException.ProfileNotFoundException(username));

        return PointResponse.builder()
                .pointBalance(member.getPointBalance())
                .boundPoint(member.getBoundPoint())
                .totalEarnedPoints(member.getTotalEarnedPoints())
                .dailyEarnedPoints(member.getDailyEarnedPoints())
                .build();
    }
}