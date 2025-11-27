package com.ohyes.GrowUpMoney.domain.auth.enums;

public enum MemberStatus {
    ACTIVE("정상"),
    SUSPENDED("정지"),
    WITHDRAWN("탈퇴");

    private final String description;

    MemberStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
