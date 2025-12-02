package com.ohyes.GrowUpMoney.domain.mypage.service;

import com.ohyes.GrowUpMoney.domain.mypage.entity.UserProgress;
import com.ohyes.GrowUpMoney.domain.mypage.repository.UserProgressRepository;
import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProgressService {

    private final UserProgressRepository userProgressRepository;
    private final MemberRepository memberRepository;

    private static final int TOTAL_THEME_COUNT = 10;
    private static final int UNITS_PER_THEME = 5;

    // 사용자의 전체 진척도 조회
    public List<UserProgress> getUserProgress(Long memberId) {
        return userProgressRepository.findByMemberId(memberId);
    }

    // 사용자의 특정 테마 진척도 조회
    public List<UserProgress> getUserProgressByTheme(Long memberId, Long themeId) {
        return userProgressRepository.findByMemberIdAndThemeId(memberId, themeId);
    }

    // 전체 진행률 계산
    public Double calculateOverallProgress(Long memberId) {
        Long completedUnits = userProgressRepository.countCompletedByMemberId(memberId);
        int totalUnits = TOTAL_THEME_COUNT * UNITS_PER_THEME;
        return (completedUnits * 100.0) / totalUnits;
    }

    // 특정 테마 진행률 계산
    public Double calculateThemeProgress(Long memberId, Long themeId) {
        Long completedUnits = userProgressRepository.countCompletedByMemberIdAndThemeId(memberId, themeId);
        return (completedUnits * 100.0) / UNITS_PER_THEME;
    }

    // 완료된 테마 수 조회
    public Long getCompletedThemeCount(Long memberId) {
        // 각 테마별로 완료된 단원 수 확인
        long completedThemes = 0;
        for (long themeId = 1; themeId <= TOTAL_THEME_COUNT; themeId++) {
            Long completedUnits = userProgressRepository
                    .countCompletedByMemberIdAndThemeId(memberId, themeId);
            if (completedUnits == UNITS_PER_THEME) {
                completedThemes++;
            }
        }
        return completedThemes;
    }

    // 단원 시작 또는 진척도 생성
    @Transactional
    public UserProgress startUnit(String username, Long themeId, Long unitId) {
        log.info("단원 시작: username={}, themeId={}, unitId={}", username, themeId, unitId);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        // 이미 진척도가 있는지 확인
        Optional<UserProgress> existingProgress = userProgressRepository
                .findByMemberIdAndThemeIdAndUnitId(member.getId(), themeId, unitId);

        if (existingProgress.isPresent()) {
            UserProgress progress = existingProgress.get();
            progress.incrementAttemptCount();
            return userProgressRepository.save(progress);
        }

        // 새로운 진척도 생성
        UserProgress progress = UserProgress.builder()
                .member(member)
                .themeId(themeId)
                .unitId(unitId)
                .isCompleted(false)
                .attemptCount(1)
                .build();

        return userProgressRepository.save(progress);
    }

    // 단원 완료 처리
    @Transactional
    public UserProgress completeUnit(String username, Long themeId, Long unitId, Integer quizScore) {
        log.info("단원 완료: username={}, themeId={}, unitId={}, score={}",
                username, themeId, unitId, quizScore);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        UserProgress progress = userProgressRepository
                .findByMemberIdAndThemeIdAndUnitId(member.getId(), themeId, unitId)
                .orElseThrow(() -> new IllegalArgumentException("진척도를 찾을 수 없습니다."));

        progress.complete(quizScore);
        return userProgressRepository.save(progress);
    }

    // 특정 단원 완료 여부 확인
    public boolean isUnitCompleted(Long memberId, Long themeId, Long unitId) {
        Optional<UserProgress> progress = userProgressRepository
                .findByMemberIdAndThemeIdAndUnitId(memberId, themeId, unitId);

        return progress.isPresent() && progress.get().getIsCompleted();
    }
}