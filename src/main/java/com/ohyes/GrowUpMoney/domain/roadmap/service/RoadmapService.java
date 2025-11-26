package com.ohyes.GrowUpMoney.domain.roadmap.service;

import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.LessonResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.ProgressResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.RoadmapResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.dto.response.ThemeResponse;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.ProgressStatus;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Theme;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.UserLessonProgress;
import com.ohyes.GrowUpMoney.domain.roadmap.exception.RoadmapException;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.LessonRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.ThemeRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.UserLessonProgressRepository;
import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RoadmapService {

    private final ThemeRepository themeRepository;
    private final LessonRepository lessonRepository;
    private final UserLessonProgressRepository progressRepository;
    private final MemberRepository memberRepository;

    // 전체 로드맵 목록 조회 (사용자별 진행률 포함)
    // GET /roadmap
    @Transactional(readOnly = true)
    public RoadmapResponse getAllThemes(String username) {
        // 1. 모든 테마 조회 (순서대로)
        List<Theme> themes = themeRepository.findAllByOrderByOrderIndexAsc();

        // 2. 사용자의 전체 진행 상황 조회
        List<UserLessonProgress> allProgress = progressRepository.findByUsername(username);
        Map<Long, UserLessonProgress> progressMap = allProgress.stream()
                .collect(Collectors.toMap(
                        p -> p.getLesson().getId(),
                        p -> p
                ));

        // 3. 각 테마별 진행 상황 계산
        List<ThemeResponse> themeResponses = new ArrayList<>();
        for (Theme theme : themes) {
            Integer completedCount = 0;

            // 해당 테마의 단원들 중 완료된 것 카운트
            for (Lesson lesson : theme.getLessons()) {
                UserLessonProgress progress = progressMap.get(lesson.getId());
                if (progress != null && progress.getStatus() == ProgressStatus.COMPLETED) {
                    completedCount++;
                }
            }

            int totalLessons = theme.getLessons().size();
            Double progressPercentage = totalLessons > 0
                    ? (double) completedCount / totalLessons * 100
                    : 0.0;

            themeResponses.add(ThemeResponse.from(theme, completedCount, progressPercentage));
        }

        // 4. 전체 진행률 계산 (Java에서)
        Long totalLessons = lessonRepository.countAllLessons();
        Long completedLessons = progressRepository.countCompletedLessonsByUsername(username);
        Double overallProgress = calculateProgress(completedLessons, totalLessons);

        return RoadmapResponse.of(username, themeResponses, overallProgress);
    }

    // 특정 테마의 단원 목록 조회
    // GET /roadmap/theme/{themeId}
    @Transactional(readOnly = true)
    public ThemeResponse getThemeWithLessons(Long themeId, String username) {
        // 1. 테마와 단원들 조회
        Theme theme = themeRepository.findByIdWithLessons(themeId)
                .orElseThrow(() -> new RoadmapException.ThemeNotFoundException(themeId));

        // 2. 해당 테마의 사용자 진행 상황 조회
        List<UserLessonProgress> progresses = progressRepository.findByUsernameAndThemeId(username, themeId);
        Map<Long, UserLessonProgress> progressMap = progresses.stream()
                .collect(Collectors.toMap(
                        p -> p.getLesson().getId(),
                        p -> p
                ));

        // 3. 단원 정보와 진행 상황 결합
        List<LessonResponse> lessonResponses = theme.getLessons().stream()
                .sorted((l1, l2) -> l1.getOrderIndex().compareTo(l2.getOrderIndex()))
                .map(lesson -> {
                    UserLessonProgress progress = progressMap.get(lesson.getId());
                    if (progress != null) {
                        return LessonResponse.fromWithProgress(
                                lesson,
                                progress.getStatus(),
                                progress.getCorrectCount(),
                                progress.getTotalAttempted(),
                                progress.getAccuracy(),
                                progress.getUpdatedAt()
                        );
                    } else {
                        return LessonResponse.from(lesson);
                    }
                })
                .collect(Collectors.toList());

        // 4. 완료된 단원 개수 계산
        Integer completedCount = (int) progresses.stream()
                .filter(p -> p.getStatus() == ProgressStatus.COMPLETED)
                .count();

        Double progressPercentage = theme.getLessons().size() > 0
                ? (double) completedCount / theme.getLessons().size() * 100
                : 0.0;

        return ThemeResponse.fromWithLessons(theme, lessonResponses, completedCount, progressPercentage);
    }

    // 사용자별 학습 진행률 조회
    // GET /roadmap/progress
    @Transactional(readOnly = true)
    public ProgressResponse getUserProgress(String username) {
        // 1. 모든 테마 조회
        List<Theme> themes = themeRepository.findAllByOrderByOrderIndexAsc();

        // 2. 전체 단원 개수
        Long totalLessons = lessonRepository.countAllLessons();

        // 3. 완료된 단원 개수
        Long totalCompletedLessons = progressRepository.countCompletedLessonsByUsername(username);

        // 4. 전체 진행률 계산 (Java에서)
        Double overallProgress = calculateProgress(totalCompletedLessons, totalLessons);

        // 5. 테마별 진행 상황 계산
        List<ProgressResponse.ThemeProgressResponse> themeProgresses = new ArrayList<>();

        for (Theme theme : themes) {
            Long totalLessonsInTheme = lessonRepository.countByThemeId(theme.getId());
            Long completedInTheme = progressRepository.countCompletedLessonsByUsernameAndThemeId(
                    username, theme.getId()
            );

            Double themeProgress = calculateProgress(completedInTheme, totalLessonsInTheme);

            themeProgresses.add(ProgressResponse.ThemeProgressResponse.builder()
                    .themeId(theme.getId())
                    .themeTitle(theme.getTitle())
                    .orderIndex(theme.getOrderIndex())
                    .totalLessons(totalLessonsInTheme.intValue())
                    .completedLessons(completedInTheme.intValue())
                    .progressPercentage(themeProgress)
                    .isCompleted(completedInTheme.equals(totalLessonsInTheme))
                    .build());
        }

        // 6. 정답 통계
        Integer totalCorrect = progressRepository.getTotalCorrectCount(username);
        Integer totalAttempted = progressRepository.getTotalAttemptedCount(username);

        if (totalCorrect == null) totalCorrect = 0;
        if (totalAttempted == null) totalAttempted = 0;

        return ProgressResponse.of(
                username,
                themes.size(),
                totalLessons.intValue(),
                totalCompletedLessons.intValue(),
                overallProgress,
                totalCorrect,
                totalAttempted,
                themeProgresses
        );
    }

    // 진행률 계산 헬퍼 메서드
    private Double calculateProgress(Long completed, Long total) {
        if (total == null || total == 0) {
            return 0.0;
        }
        if (completed == null) {
            completed = 0L;
        }
        return (completed.doubleValue() / total) * 100.0;
    }

    // 단원 학습 시작
    @Transactional
    public void startLesson(String username, Long lessonId) {
        // 회원 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + username));

        // 단원 조회
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RoadmapException.LessonNotFoundException(lessonId));

        UserLessonProgress progress = progressRepository.findByUsernameAndLessonId(username, lessonId)
                .orElseGet(() -> {
                    UserLessonProgress newProgress = UserLessonProgress.builder()
                            .member(member)
                            .lesson(lesson)
                            .status(ProgressStatus.NOT_STARTED)
                            .build();
                    return progressRepository.save(newProgress);
                });

        progress.startLesson();
        log.info("사용자 {}의 단원 {} 학습 시작", username, lessonId);
    }

    // 단원 완료 처리
    @Transactional
    public void completeLesson(String username, Long lessonId) {
        UserLessonProgress progress = progressRepository.findByUsernameAndLessonId(username, lessonId)
                .orElseThrow(() -> new RoadmapException.ProgressNotFoundException(username, lessonId));

        progress.completeLesson();
        log.info("사용자 {}의 단원 {} 완료", username, lessonId);
    }

    // 퀴즈 결과 업데이트
    @Transactional
    public void updateQuizResult(String username, Long lessonId, boolean isCorrect) {
        UserLessonProgress progress = progressRepository.findByUsernameAndLessonId(username, lessonId)
                .orElseThrow(() -> new RoadmapException.ProgressNotFoundException(username, lessonId));

        progress.updateQuizResult(isCorrect);
        log.info("사용자 {}의 단원 {} 퀴즈 결과 업데이트: {}", username, lessonId, isCorrect);
    }
}