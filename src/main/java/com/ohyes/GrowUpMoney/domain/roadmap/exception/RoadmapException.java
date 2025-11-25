package com.ohyes.GrowUpMoney.domain.roadmap.exception;

public class RoadmapException extends RuntimeException {

    public RoadmapException(String message) {
        super(message);
    }

    public RoadmapException(String message, Throwable cause) {
        super(message, cause);
    }

    // 테마를 찾을 수 없는 경우
    public static class ThemeNotFoundException extends RoadmapException {
        public ThemeNotFoundException(Long themeId) {
            super("테마를 찾을 수 없습니다. ID: " + themeId);
        }
    }

    // 단원을 찾을 수 없는 경우
    public static class LessonNotFoundException extends RoadmapException {
        public LessonNotFoundException(Long lessonId) {
            super("단원을 찾을 수 없습니다. ID: " + lessonId);
        }
    }

    // 진행 상황을 찾을 수 없는 경우
    public static class ProgressNotFoundException extends RoadmapException {
        public ProgressNotFoundException(String username, Long lessonId) {
            super(String.format("진행 상황을 찾을 수 없습니다. 사용자: %s, 단원ID: %d", username, lessonId));
        }
    }

    // 잘못된 진행 상태인 경우
    public static class InvalidProgressStatusException extends RoadmapException {
        public InvalidProgressStatusException(String message) {
            super("잘못된 진행 상태입니다: " + message);
        }
    }

    // 단원이 잠겨있는 경우
    public static class LessonNotUnlockedException extends RoadmapException {
        public LessonNotUnlockedException(Long lessonId) {
            super("아직 잠겨있는 단원입니다. 이전 단원을 먼저 완료해주세요. 단원ID: " + lessonId);
        }
    }
}