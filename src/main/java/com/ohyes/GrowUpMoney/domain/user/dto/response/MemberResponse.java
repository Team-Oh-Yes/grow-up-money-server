package com.ohyes.GrowUpMoney.domain.user.dto.response;

import com.ohyes.GrowUpMoney.domain.user.entity.Member;
import com.ohyes.GrowUpMoney.domain.user.enums.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
                .created_at(member.getCreated_at())
                .updated_at(member.getUpdated_at())
                .build();
    }
}
