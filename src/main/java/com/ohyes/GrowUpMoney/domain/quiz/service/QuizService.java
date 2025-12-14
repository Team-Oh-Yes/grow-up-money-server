package com.ohyes.GrowUpMoney.domain.quiz.service;

import com.ohyes.GrowUpMoney.domain.member.service.HeartService;
import com.ohyes.GrowUpMoney.domain.quiz.dto.request.AnswerSubmitRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.response.*;
import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import com.ohyes.GrowUpMoney.domain.quiz.entity.QuizAttempt;
import com.ohyes.GrowUpMoney.domain.quiz.enums.QuizRewardConstants;
import com.ohyes.GrowUpMoney.domain.quiz.exception.QuizException;
import com.ohyes.GrowUpMoney.domain.quiz.repository.QuestionRepository;
import com.ohyes.GrowUpMoney.domain.quiz.repository.QuizAttemptRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.ProgressStatus;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.LessonRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.UserLessonProgressRepository;
import com.ohyes.GrowUpMoney.domain.member.entity.Member;
import com.ohyes.GrowUpMoney.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuizService {

    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final MemberRepository memberRepository;
    private final LessonRepository lessonRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final HeartService heartService;  // member 도메인의 HeartService
    private final QuizRewardService quizRewardService;

    public QuizListResponse getQuizzesByLesson(Long lessonId, String username) {
        log.info("단원 퀴즈 목록 조회: lessonId={}, username={}", lessonId, username);

        Lesson lesson = findLesson(lessonId);
        Member member = findMember(username);

        // 하트 리셋 체크
        heartService.checkAndResetHearts(username);

        boolean learningCompleted = userLessonProgressRepository
                .countByUsernameAndLessonIdAndStatus(username, lessonId, ProgressStatus.COMPLETED) > 0;

        if (!learningCompleted) {
            throw new QuizException.LearningNotCompletedException(lessonId);
        }

        checkDailyThemeLimit(username, lesson.getTheme().getId());

        List<Question> questions = getQuestionsForUser(lessonId, member);

        List<QuestionResponse> questionResponses = questions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());

        long completedCount = quizAttemptRepository.countCorrectByUsernameAndLessonId(username, lessonId);
        int earnedPoints = quizAttemptRepository.sumAwardedPointsByLessonId(username, lessonId);
        int hearts = heartService.getCurrentHearts(username);

        return QuizListResponse.of(lessonId, lesson.getTitle(), lesson.getTheme().getId(),
                lesson.getTheme().getTitle(), questionResponses, (int) completedCount, earnedPoints, hearts);
    }

    @Transactional
    public QuizResultResponse submitAnswer(Long questionId, AnswerSubmitRequest request, String username) {
        log.info("퀴즈 정답 제출: questionId={}, username={}", questionId, username);

        Question question = findQuestion(questionId);
        Member member = findMember(username);

        // 하트 체크
        if (!heartService.hasEnoughHearts(username)) {
            throw new QuizException.InsufficientHeartsException();
        }

        if (question.getIsPremium() && (member.getRole() == null || !member.getRole().contains("PREMIUM"))) {
            throw new QuizException.PremiumAccessDeniedException();
        }

        if (quizAttemptRepository.existsCorrectAttempt(username, questionId)) {
            throw new QuizException.AlreadyCorrectException(questionId);
        }

        boolean isCorrect = question.checkAnswer(request.getAnswer());
        int awardedPoints = 0;
        int attemptCount = 1;

        QuizAttempt existingAttempt = quizAttemptRepository
                .findLatestByUsernameAndQuestionId(username, questionId)
                .orElse(null);

        if (existingAttempt != null) {
            attemptCount = existingAttempt.getAttemptCount() + 1;
        }

        int remainingHearts;

        if (isCorrect) {
            awardedPoints = question.getPointReward();
            // 귀속 포인트로 지급 (Member.addBoundPoint 사용)
            member.addBoundPoint(awardedPoints);
            memberRepository.save(member);
            quizRewardService.checkAndRewardLessonCompletion(username, question.getLesson().getId());
            remainingHearts = heartService.getCurrentHearts(username);
        } else {
            // 오답 시 하트 차감
            remainingHearts = heartService.deductHeart(username);
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .member(member)
                .question(question)
                .userAnswer(request.getAnswer())
                .isCorrect(isCorrect)
                .awardedPoints(awardedPoints)
                .attemptCount(attemptCount)
                .build();
        quizAttemptRepository.save(attempt);

        log.info("퀴즈 채점 완료: questionId={}, isCorrect={}, awardedPoints={}", questionId, isCorrect, awardedPoints);

        if (isCorrect) {
            return QuizResultResponse.correct(questionId, request.getAnswer(), question.getAnswerKey(),
                    awardedPoints, question.getExplanation(), remainingHearts, attemptCount);
        } else {
            return QuizResultResponse.incorrect(questionId, request.getAnswer(), question.getAnswerKey(),
                    question.getExplanation(), remainingHearts, attemptCount);
        }
    }

    public WrongAnswerListResponse getWrongAnswersByLesson(Long lessonId, String username) {
        log.info("단원 틀린 문제 조회: lessonId={}, username={}", lessonId, username);

        Lesson lesson = findLesson(lessonId);
        List<Long> wrongQuestionIds = quizAttemptRepository.findWrongQuestionIdsByLessonId(username, lessonId);
        List<Question> wrongQuestions = questionRepository.findAllById(wrongQuestionIds);
        List<QuestionResponse> questionResponses = wrongQuestions.stream()
                .map(QuestionResponse::simple)
                .collect(Collectors.toList());

        return WrongAnswerListResponse.ofLesson(lessonId, lesson.getTitle(), questionResponses);
    }

    public WrongAnswerListResponse getAllWrongAnswers(String username) {
        log.info("전체 틀린 문제 조회: username={}", username);

        List<Long> wrongQuestionIds = quizAttemptRepository.findAllWrongQuestionIds(username);
        List<Question> wrongQuestions = questionRepository.findAllById(wrongQuestionIds);
        List<QuestionResponse> questionResponses = wrongQuestions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());

        return WrongAnswerListResponse.ofAll(questionResponses);
    }

    public LessonQuizSummaryResponse getLessonQuizSummary(Long lessonId, String username) {
        log.info("단원 퀴즈 요약 조회: lessonId={}, username={}", lessonId, username);

        Lesson lesson = findLesson(lessonId);
        Member member = findMember(username);

        long totalQuestions = questionRepository.countByLessonId(lessonId);
        long correctCount = quizAttemptRepository.countCorrectByUsernameAndLessonId(username, lessonId);
        int earnedPoints = quizAttemptRepository.sumAwardedPointsByLessonId(username, lessonId);
        boolean isCompleted = correctCount >= totalQuestions;

        int bonusPoints = 0;
        boolean gachaTicketAwarded = false;
        int gachaTicketCount = 0;

        if (isCompleted) {
            boolean isPremium = member.getRole() != null && member.getRole().contains("PREMIUM");
            bonusPoints = isPremium ? QuizRewardConstants.PREMIUM_LESSON_BONUS : QuizRewardConstants.NORMAL_LESSON_BONUS;
            gachaTicketAwarded = true;
            gachaTicketCount = QuizRewardConstants.GACHA_TICKET_PER_LESSON;
        }

        return LessonQuizSummaryResponse.of(lessonId, lesson.getTitle(), lesson.getTheme().getId(),
                lesson.getTheme().getTitle(), (int) totalQuestions, (int) correctCount, earnedPoints,
                bonusPoints, isCompleted, gachaTicketAwarded, gachaTicketCount);
    }

    private void checkDailyThemeLimit(String username, Long currentThemeId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        List<Long> todayThemeIds = quizAttemptRepository.findTodayPlayedThemeIds(username, startOfDay);

        if (todayThemeIds.contains(currentThemeId)) {
            return;
        }

        if (todayThemeIds.size() >= QuizRewardConstants.DAILY_THEME_LIMIT) {
            throw new QuizException.DailyThemeLimitExceededException(QuizRewardConstants.DAILY_THEME_LIMIT);
        }
    }

    private List<Question> getQuestionsForUser(Long lessonId, Member member) {
        // 프리미엄 회원 확인 (role 기반)
        if (member.getRole() != null && member.getRole().contains("PREMIUM")) {
            return questionRepository.findByLessonIdOrderByOrderIndex(lessonId);
        } else {
            return questionRepository.findNormalQuestionsByLessonId(lessonId);
        }
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findByIdWithLessonAndTheme(questionId);
    }

    private Lesson findLesson(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new QuizException.LessonNotFoundException(lessonId));
    }

    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
    }
}