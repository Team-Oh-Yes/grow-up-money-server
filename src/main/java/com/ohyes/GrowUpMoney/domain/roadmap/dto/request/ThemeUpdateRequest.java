package com.ohyes.GrowUpMoney.domain.roadmap.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 테마 수정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeUpdateRequest {

    // 테마 제목 (선택)
    private String title;

    // 순서 (선택)
    @Positive(message = "순서는 1 이상이어야 합니다")
    private Integer orderIndex;
}