package com.ohyes.GrowUpMoney.domain.quiz.enums;

public class QuizRewardConstants {

    private QuizRewardConstants() {}

    // === 일반 문제 보상 ===
    public static final int NORMAL_QUESTION_POINT = 100;
    public static final int NORMAL_LESSON_BONUS = 100;
    public static final int NORMAL_THEME_BONUS = 500;
    public static final int NORMAL_TOTAL_BONUS = 5000;

    // === 프리미엄 문제 보상 ===
    public static final int PREMIUM_QUESTION_POINT = 500;
    public static final int PREMIUM_LESSON_BONUS = 500;
    public static final int PREMIUM_THEME_BONUS = 2500;
    public static final int PREMIUM_TOTAL_BONUS = 25000;

    // === 하트 시스템 ===
    public static final int DEFAULT_HEARTS = 5;
    public static final int HEART_COST_POINTS = 50;
    public static final int MAX_HEARTS = 10;

    // === 플레이 제한 ===
    public static final int DAILY_THEME_LIMIT = 2;

    // === 문제 구성 ===
    public static final int MULTIPLE_CHOICE_COUNT = 5;
    public static final int SHORT_ANSWER_COUNT = 5;
    public static final int TOTAL_QUESTION_COUNT = 10;

    // === 뽑기권 ===
    public static final int GACHA_TICKET_PER_LESSON = 1;
}