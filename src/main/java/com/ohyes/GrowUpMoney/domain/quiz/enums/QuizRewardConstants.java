package com.ohyes.GrowUpMoney.domain.quiz.enums;

// 지금이거안쓰고있음
public enum QuizRewardConstants {
    LESSON_CLEAR(10),
    THEME_CLEAR(50),
    ALL_CLEAR(200);

    private final int reward;

    QuizRewardConstants(int reward) {
        this.reward = reward;
    }

    public int getReward() {
        return reward;
    }
}
