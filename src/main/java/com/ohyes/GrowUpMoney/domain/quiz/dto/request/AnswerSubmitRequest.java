package com.ohyes.GrowUpMoney.domain.quiz.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerSubmitRequest {

    @NotBlank(message = "답안은 필수입니다.")
    private String answer;
}