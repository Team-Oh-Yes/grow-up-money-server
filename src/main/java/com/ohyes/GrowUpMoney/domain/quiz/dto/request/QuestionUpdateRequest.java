package com.ohyes.GrowUpMoney.domain.quiz.dto.request;

import com.ohyes.GrowUpMoney.domain.quiz.enums.Difficulty;
import com.ohyes.GrowUpMoney.domain.quiz.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionUpdateRequest {

    private QuestionType type;
    private Difficulty difficulty;
    private String stem;
    private List<String> options;
    private String answerKey;
    private Integer pointReward;
    private Boolean isPremium;
    private Integer orderIndex;
    private String explanation;
}