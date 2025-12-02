package com.ohyes.GrowUpMoney.domain.mypage.service;

import com.ohyes.GrowUpMoney.domain.mypage.entity.Badge;
import com.ohyes.GrowUpMoney.domain.mypage.entity.UserBadge;
import com.ohyes.GrowUpMoney.domain.mypage.repository.BadgeRepository;
import com.ohyes.GrowUpMoney.domain.mypage.repository.UserBadgeRepository;
import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
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
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    // 모든 배지 조회
    public List<Badge> getAllBadges() {
        return badgeRepository.findAllOrderByLevel();
    }

    // 사용자가 획득한 배지 ID 목록 조회
    public List<Long> getUserBadgeIds(Long memberId) {
        return userBadgeRepository.findBadgeIdsByMemberId(memberId);
    }

    // 사용자의 배지 획득 여부 확인 및 자동 부여
    @Transactional
    public void checkAndAwardBadges(Member member) {
        log.info("배지 획득 여부 확인: memberId={}, totalPoints={}",
                member.getId(), member.getTotalEarnedPoints());

        // 획득 가능한 배지 조회
        List<Badge> acquirableBadges = badgeRepository.findAcquirableBadges(member.getTotalEarnedPoints());

        // 이미 획득한 배지 ID 목록
        List<Long> acquiredBadgeIds = getUserBadgeIds(member.getId());

        // 아직 획득하지 않은 배지 부여
        List<Badge> newBadges = acquirableBadges.stream()
                .filter(badge -> !acquiredBadgeIds.contains(badge.getId()))
                .collect(Collectors.toList());

        for (Badge badge : newBadges) {
            UserBadge userBadge = UserBadge.builder()
                    .member(member)
                    .badge(badge)
                    .build();

            userBadgeRepository.save(userBadge);
            log.info("새 배지 획득: memberId={}, badgeName={}", member.getId(), badge.getBadgeName());
        }
    }

    // 배지 초기 데이터 생성 (애플리케이션 시작 시 실행)
    @Transactional
    public void initializeBadges() {
        if (badgeRepository.count() > 0) {
            log.info("배지 데이터가 이미 존재합니다.");
            return;
        }

        log.info("배지 초기 데이터 생성 시작");

        // 배지 레벨 정의
        Badge[] badges = {
                Badge.builder()
                        .badgeName("경제 새싹")
                        .badgeDescription("1,000 포인트 달성")
                        .badgeImageUrl("/images/badge_1.png")
                        .requiredPoints(1000L)
                        .badgeLevel(1)
                        .build(),

                Badge.builder()
                        .badgeName("경제 초보")
                        .badgeDescription("5,000 포인트 달성")
                        .badgeImageUrl("/images/badge_2.png")
                        .requiredPoints(5000L)
                        .badgeLevel(2)
                        .build(),

                Badge.builder()
                        .badgeName("경제 중수")
                        .badgeDescription("10,000 포인트 달성")
                        .badgeImageUrl("/images/badge_3.png")
                        .requiredPoints(10000L)
                        .badgeLevel(3)
                        .build(),

                Badge.builder()
                        .badgeName("경제 고수")
                        .badgeDescription("50,000 포인트 달성")
                        .badgeImageUrl("/images/badge_4.png")
                        .requiredPoints(50000L)
                        .badgeLevel(4)
                        .build(),

                Badge.builder()
                        .badgeName("경제 마스터")
                        .badgeDescription("100,000 포인트 달성")
                        .badgeImageUrl("/images/badge_5.png")
                        .requiredPoints(100000L)
                        .badgeLevel(5)
                        .build()
        };

        for (Badge badge : badges) {
            badgeRepository.save(badge);
        }

        log.info("배지 초기 데이터 생성 완료: {} 개", badges.length);
    }
}