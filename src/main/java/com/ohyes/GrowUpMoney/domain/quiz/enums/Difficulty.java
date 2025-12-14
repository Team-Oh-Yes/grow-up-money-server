package com.ohyes.GrowUpMoney.domain.quiz.enums;

public enum Difficulty {
    NORMAL(100),   // 일반
    PREMIUM(500);   // 프리미엄

    private final int pointReward;

    Difficulty(int pointReward) {
        this.pointReward = pointReward;
    }

    public int getPointReward() {
        return pointReward;
    }
}
