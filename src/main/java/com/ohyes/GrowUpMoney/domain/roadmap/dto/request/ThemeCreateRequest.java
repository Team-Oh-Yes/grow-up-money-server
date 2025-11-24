package com.ohyes.GrowUpMoney.domain.roadmap.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 테마 생성 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeCreateRequest {

    // 테마 제목
    @NotBlank(message = "테마 제목은 필수입니다")
    private String title;

    // 순서 (1부터 시작)
    @NotNull(message = "순서는 필수입니다")
    @Positive(message = "순서는 1 이상이어야 합니다")
    private Integer orderIndex;
}