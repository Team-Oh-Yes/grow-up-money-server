package com.ohyes.GrowUpMoney.domain.auth.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SuspensionType {
    ONE_DAY(1, "1일"),
    THREE_DAYS(3, "3일"),
    ONE_WEEK(7, "1주"),
    ONE_MONTH(30, "1달"),
    PERMANENT(-1, "영구");

    private final int days;
    private final String description;

    SuspensionType(int days, String description) {
        this.days = days;
        this.description = description;
    }

    public int getDays() {
        return days;
    }

    public String getDescription() {
        return description;
    }

    // JSON에서 문자열을 받아서 Enum으로 변환
    @JsonCreator
    public static SuspensionType fromString(String value) {
        if (value == null) {
            return null;
        }

        try {
            return SuspensionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid SuspensionType: " + value +
                            ". Valid values are: ONE_DAY, THREE_DAYS, ONE_WEEK, ONE_MONTH, PERMANENT"
            );
        }
    }

    // Enum을 JSON 문자열로 변환
    @JsonValue
    public String toValue() {
        return this.name();
    }
}

