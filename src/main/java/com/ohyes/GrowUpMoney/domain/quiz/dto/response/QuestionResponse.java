package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder

// 해당 문제
public class QuestionResponse {

    private Long id;
    private String stem;
    private List<String> options;
    private String answerKey;
    private String explanation;

    public static QuestionResponse from(Question q, boolean showAnswer) {
        return QuestionResponse.builder()
                .id(q.getId())
                .stem(q.getStem())
                .options(q.getOptions())
                .answerKey(showAnswer ? q.getAnswerKey() : null)
                .explanation(q.getExplanation())
                .build();
    }
}
