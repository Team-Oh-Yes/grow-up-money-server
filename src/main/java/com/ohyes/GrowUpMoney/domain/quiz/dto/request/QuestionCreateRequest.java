package com.ohyes.GrowUpMoney.domain.quiz.dto.request;

import com.ohyes.GrowUpMoney.domain.quiz.enums.Difficulty;
import com.ohyes.GrowUpMoney.domain.quiz.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionCreateRequest {

    @NotNull(message = "단원 ID는 필수입니다.")
    private Long lessonId;

    @NotNull(message = "문제 유형은 필수입니다.")
    private QuestionType type;

    @NotNull(message = "난이도는 필수입니다.")
    private Difficulty difficulty;

    @NotBlank(message = "문제 내용은 필수입니다.")
    private String stem;

    private List<String> options;

    @NotBlank(message = "정답은 필수입니다.")
    private String answerKey;

    @Positive(message = "포인트 보상은 양수여야 합니다.")
    private Integer pointReward;

    @NotNull(message = "프리미엄 여부는 필수입니다.")
    private Boolean isPremium;

    @NotNull(message = "문제 순서는 필수입니다.")
    @Positive(message = "문제 순서는 양수여야 합니다.")
    private Integer orderIndex;

    private String explanation;
}