package com.ohyes.GrowUpMoney.domain.user.dto.response;

import com.ohyes.GrowUpMoney.domain.user.enums.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberStatusResponse {
    private String username;
    private MemberStatus status;
    private String statusDescription;
    private LocalDateTime suspendedUntil;
    private String suspensionReason;
    private boolean isActive;
}
