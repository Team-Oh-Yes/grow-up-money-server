package com.ohyes.GrowUpMoney.domain.roadmap.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 단원 수정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonUpdateRequest {

    // 테마 ID (선택 - 테마 변경 시)
    @Positive(message = "테마 ID는 1 이상이어야 합니다")
    private Long themeId;

    // 단원 제목 (선택)
    private String title;

    // 순서 (선택)
    @Positive(message = "순서는 1 이상이어야 합니다")
    private Integer orderIndex;
}