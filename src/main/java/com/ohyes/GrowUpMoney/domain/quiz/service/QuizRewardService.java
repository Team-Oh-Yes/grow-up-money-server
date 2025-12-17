package com.ohyes.GrowUpMoney.domain.quiz.service;

import com.ohyes.GrowUpMoney.domain.quiz.enums.QuizRewardConstants;
import com.ohyes.GrowUpMoney.domain.quiz.exception.QuizException;
import com.ohyes.GrowUpMoney.domain.quiz.repository.QuestionRepository;
import com.ohyes.GrowUpMoney.domain.quiz.repository.QuizAttemptRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.ProgressStatus;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Theme;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.UserLessonProgress;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.LessonRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.ThemeRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.UserLessonProgressRepository;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuizRewardService {

    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final MemberRepository memberRepository;
    private final LessonRepository lessonRepository;
    private final ThemeRepository themeRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;

    @Transactional
    public LessonCompletionResult checkAndRewardLessonCompletion(String username, Long lessonId) {
        log.info("단원 완료 확인: username={}, lessonId={}", username, lessonId);

        Member member = findMember(username);
        Lesson lesson = findLesson(lessonId);

        UserLessonProgress progress = userLessonProgressRepository
                .findByUsernameAndLessonId(username, lessonId)
                .orElse(null);

        if (progress != null && progress.getStatus() == ProgressStatus.COMPLETED) {
            log.debug("이미 단원 완료 보상 지급됨: lessonId={}", lessonId);
            return LessonCompletionResult.alreadyCompleted();
        }

        long totalQuestions = questionRepository.countByLessonId(lessonId);
        long correctCount = quizAttemptRepository.countCorrectByUsernameAndLessonId(username, lessonId);

        if (correctCount < totalQuestions) {
            return LessonCompletionResult.notCompleted((int) correctCount, (int) totalQuestions);
        }

        // 프리미엄 확인 (role 기반)
        boolean isPremium = member.getRole() != null && member.getRole().contains("PREMIUM");
        int bonusPoints = isPremium ? QuizRewardConstants.PREMIUM_LESSON_BONUS : QuizRewardConstants.NORMAL_LESSON_BONUS;

        // 귀속 포인트로 지급 (Member.addBoundPoint 사용)
        member.addBoundPoint(bonusPoints);
        // TODO: 뽑기권 지급 로직 추가 필요
        memberRepository.save(member);

        if (progress == null) {
            progress = UserLessonProgress.builder()
                    .member(member)
                    .lesson(lesson)
                    .status(ProgressStatus.COMPLETED)
                    .build();
        } else {
            progress.completeLesson((int) correctCount, (int) totalQuestions);
        }
        userLessonProgressRepository.save(progress);

        log.info("단원 완료 보상 지급: lessonId={}, bonusPoints={}, gachaTicket=1", lessonId, bonusPoints);

        checkAndRewardThemeCompletion(username, lesson.getTheme().getId());

        return LessonCompletionResult.completed(bonusPoints, 1);
    }

    @Transactional
    public ThemeCompletionResult checkAndRewardThemeCompletion(String username, Long themeId) {
        log.info("테마 완료 확인: username={}, themeId={}", username, themeId);

        Member member = findMember(username);

        List<Lesson> lessons = lessonRepository.findByThemeIdOrderByOrderIndexAsc(themeId);

        for (Lesson lesson : lessons) {
            long totalQuestions = questionRepository.countByLessonId(lesson.getId());
            long correctCount = quizAttemptRepository.countCorrectByUsernameAndLessonId(username, lesson.getId());

            if (correctCount < totalQuestions) {
                return ThemeCompletionResult.notCompleted();
            }
        }

        // 프리미엄 확인 (role 기반)
        boolean isPremium = member.getRole() != null && member.getRole().contains("PREMIUM");
        int bonusPoints = isPremium ? QuizRewardConstants.PREMIUM_THEME_BONUS : QuizRewardConstants.NORMAL_THEME_BONUS;

        // 귀속 포인트로 지급
        member.addBoundPoint(bonusPoints);
        memberRepository.save(member);

        log.info("테마 완료 보상 지급: themeId={}, bonusPoints={}", themeId, bonusPoints);

        checkAndRewardTotalCompletion(username);

        return ThemeCompletionResult.completed(bonusPoints);
    }

    @Transactional
    public TotalCompletionResult checkAndRewardTotalCompletion(String username) {
        log.info("전체 완료 확인: username={}", username);

        Member member = findMember(username);
        List<Theme> themes = themeRepository.findAll();

        for (Theme theme : themes) {
            List<Lesson> lessons = lessonRepository.findByThemeIdOrderByOrderIndexAsc(theme.getId());

            for (Lesson lesson : lessons) {
                long totalQuestions = questionRepository.countByLessonId(lesson.getId());
                long correctCount = quizAttemptRepository.countCorrectByUsernameAndLessonId(username, lesson.getId());

                if (correctCount < totalQuestions) {
                    return TotalCompletionResult.notCompleted();
                }
            }
        }

        // 프리미엄 확인 (role 기반)
        boolean isPremium = member.getRole() != null && member.getRole().contains("PREMIUM");
        int bonusPoints = isPremium ? QuizRewardConstants.PREMIUM_TOTAL_BONUS : QuizRewardConstants.NORMAL_TOTAL_BONUS;

        // 귀속 포인트로 지급
        member.addBoundPoint(bonusPoints);
        memberRepository.save(member);

        log.info("전체 완료 보상 지급: username={}, bonusPoints={}", username, bonusPoints);

        return TotalCompletionResult.completed(bonusPoints);
    }

    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
    }

    private Lesson findLesson(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new QuizException.LessonNotFoundException(lessonId));
    }

    public record LessonCompletionResult(boolean completed, boolean alreadyRewarded, int correctCount, int totalQuestions, int bonusPoints, int gachaTickets) {
        public static LessonCompletionResult completed(int bonusPoints, int gachaTickets) {
            return new LessonCompletionResult(true, false, 0, 0, bonusPoints, gachaTickets);
        }
        public static LessonCompletionResult notCompleted(int correctCount, int totalQuestions) {
            return new LessonCompletionResult(false, false, correctCount, totalQuestions, 0, 0);
        }
        public static LessonCompletionResult alreadyCompleted() {
            return new LessonCompletionResult(true, true, 0, 0, 0, 0);
        }
    }

    public record ThemeCompletionResult(boolean completed, int bonusPoints) {
        public static ThemeCompletionResult completed(int bonusPoints) {
            return new ThemeCompletionResult(true, bonusPoints);
        }
        public static ThemeCompletionResult notCompleted() {
            return new ThemeCompletionResult(false, 0);
        }
    }

    public record TotalCompletionResult(boolean completed, int bonusPoints) {
        public static TotalCompletionResult completed(int bonusPoints) {
            return new TotalCompletionResult(true, bonusPoints);
        }
        public static TotalCompletionResult notCompleted() {
            return new TotalCompletionResult(false, 0);
        }
    }

    @Transactional
    public LessonCompletionResult rewardLessonCompletion(String username, Long lessonId) {
        log.info("단원 완료 보상 처리: username={}, lessonId={}", username, lessonId);

        Member member = findMember(username);
        Lesson lesson = findLesson(lessonId);

        // 이미 완료한 단원인지 확인
        UserLessonProgress progress = userLessonProgressRepository
                .findByUsernameAndLessonId(username, lessonId)
                .orElse(null);

        if (progress != null && progress.getStatus() == ProgressStatus.COMPLETED) {
            log.warn("이미 완료한 단원: username={}, lessonId={}", username, lessonId);
            return LessonCompletionResult.alreadyCompleted();
        }

        // 정답 개수 확인
        long totalQuestions = questionRepository.countByLessonId(lessonId);
        long correctCount = quizAttemptRepository.countCorrectByUsernameAndLessonId(username, lessonId);

        if (correctCount < totalQuestions) {
            return LessonCompletionResult.notCompleted((int) correctCount, (int) totalQuestions);
        }

        // 프리미엄 확인 (role 기반)
        boolean isPremium = member.getRole() != null && member.getRole().contains("PREMIUM");
        int bonusPoints = isPremium ?
                QuizRewardConstants.PREMIUM_LESSON_BONUS : QuizRewardConstants.NORMAL_LESSON_BONUS;

        // 귀속 포인트로 지급 (Member.addBoundPoint 사용)
        member.addBoundPoint(bonusPoints);

        // 뽑기권 지급 추가
        member.addGachaTickets(QuizRewardConstants.GACHA_TICKET_PER_LESSON);

        memberRepository.save(member);

        if (progress == null) {
            progress = UserLessonProgress.builder()
                    .member(member)
                    .lesson(lesson)
                    .status(ProgressStatus.COMPLETED)
                    .build();
        } else {
            progress.completeLesson((int) correctCount, (int) totalQuestions);
        }
        userLessonProgressRepository.save(progress);

        log.info("단원 완료 보상 지급: lessonId={}, bonusPoints={}, gachaTicket=1", lessonId, bonusPoints);

        checkAndRewardThemeCompletion(username, lesson.getTheme().getId());

        return LessonCompletionResult.completed(bonusPoints, 1);
    }

}