package com.ohyes.GrowUpMoney.domain.mypage.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Size(max = 250, message = "자기소개는 250자 이하여야 합니다.")
    private String introduction;

    private String profileImageUrl;

    private Long favoriteNftId;  // 대표 에쁘띠 설정

    @Size(min = 8, message = "현재 비밀번호는 최소 8자 이상이어야 합니다.")
    private String currentPassword;  // 비밀번호 변경 시 현재 비밀번호

    @Size(min = 8, message = "새 비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;  // 새 비밀번호
}