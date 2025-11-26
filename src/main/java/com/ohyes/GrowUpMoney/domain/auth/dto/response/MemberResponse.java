package com.ohyes.GrowUpMoney.domain.auth.dto.response;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.enums.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class MemberResponse {
    private Long id;
    private String username;
    private String email;
    private MemberStatus status;
    private String role;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .status(member.getStatus())
                .role(member.getRole())
                .created_at(member.getCreatedAt())
                .updated_at(member.getUpdatedAt())
                .build();
    }
}
