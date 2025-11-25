package com.ohyes.GrowUpMoney.domain.roadmap.dto.request;

import com.ohyes.GrowUpMoney.domain.roadmap.entity.ProgressStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProgressUpdateRequest {

    @NotNull(message = "레슨 ID는 필수입니다")
    private Long lessonId;

    private ProgressStatus status;

    private Boolean isCorrect;

    @Builder
    public ProgressUpdateRequest(Long lessonId, ProgressStatus status, Boolean isCorrect) {
        this.lessonId = lessonId;
        this.status = status;
        this.isCorrect = isCorrect;
    }

}
