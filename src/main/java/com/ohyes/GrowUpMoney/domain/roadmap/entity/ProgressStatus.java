package com.ohyes.GrowUpMoney.domain.roadmap.entity;

public enum ProgressStatus {

    NOT_STARTED("시작 전"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료");

    private final String description;

    ProgressStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isInProgress() {
        return this == IN_PROGRESS;
    }

    public boolean isNotStarted() {
        return this == NOT_STARTED;
    }

    public boolean canTransitionTo(ProgressStatus targetStatus) {
        return switch (this) {
            case NOT_STARTED -> targetStatus == IN_PROGRESS;
            case IN_PROGRESS -> targetStatus == COMPLETED;
            case COMPLETED -> false; // 완료 후에는 상태 변경 불가
        };
    }

}
