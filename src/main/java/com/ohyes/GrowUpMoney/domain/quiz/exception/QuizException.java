package com.ohyes.GrowUpMoney.domain.quiz.exception;

import org.springframework.http.HttpStatus;

public class QuizException extends RuntimeException {

    private final HttpStatus status;

    public QuizException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public QuizException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static class QuestionNotFoundException extends QuizException {
        public QuestionNotFoundException(Long questionId) {
            super("문제를 찾을 수 없습니다: " + questionId, HttpStatus.NOT_FOUND);
        }
    }

    public static class AlreadyCorrectException extends QuizException {
        public AlreadyCorrectException(Long questionId) {
            super("이미 정답 처리된 문제입니다: " + questionId, HttpStatus.CONFLICT);
        }
    }

    public static class InsufficientHeartsException extends QuizException {
        public InsufficientHeartsException() {
            super("하트가 부족합니다. 포인트로 하트를 충전해주세요.", HttpStatus.PAYMENT_REQUIRED);
        }
    }

    public static class MaxHeartsExceededException extends QuizException {
        public MaxHeartsExceededException(int maxHearts) {
            super("최대 하트 개수(" + maxHearts + "개)를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public static class InsufficientPointsException extends QuizException {
        public InsufficientPointsException(int required, int current) {
            super("포인트가 부족합니다. 필요: " + required + "pt, 보유: " + current + "pt", HttpStatus.PAYMENT_REQUIRED);
        }
    }

    public static class DailyThemeLimitExceededException extends QuizException {
        public DailyThemeLimitExceededException(int limit) {
            super("오늘의 테마 플레이 제한(" + limit + "개)을 초과했습니다.", HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    public static class PremiumAccessDeniedException extends QuizException {
        public PremiumAccessDeniedException() {
            super("프리미엄 문제에 접근하려면 프리미엄 구독이 필요합니다.", HttpStatus.FORBIDDEN);
        }
    }

    public static class LearningNotCompletedException extends QuizException {
        public LearningNotCompletedException(Long lessonId) {
            super("학습을 먼저 완료해야 퀴즈를 풀 수 있습니다. 단원 ID: " + lessonId, HttpStatus.PRECONDITION_FAILED);
        }
    }

    public static class LessonNotFoundException extends QuizException {
        public LessonNotFoundException(Long lessonId) {
            super("단원을 찾을 수 없습니다: " + lessonId, HttpStatus.NOT_FOUND);
        }
    }

    public static class ThemeNotFoundException extends QuizException {
        public ThemeNotFoundException(Long themeId) {
            super("테마를 찾을 수 없습니다: " + themeId, HttpStatus.NOT_FOUND);
        }
    }

    public static class RewardAlreadyClaimedException extends QuizException {
        public RewardAlreadyClaimedException(String rewardType) {
            super("이미 " + rewardType + " 보상을 받았습니다.", HttpStatus.CONFLICT);
        }
    }

    public static class LessonNotCompletedException extends QuizException {
        public LessonNotCompletedException(Long lessonId) {
            super("단원을 완료해야 보상을 받을 수 있습니다. 단원 ID: " + lessonId, HttpStatus.PRECONDITION_FAILED);
        }
    }
}