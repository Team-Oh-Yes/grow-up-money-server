package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Builder

// 사용자가 풀어야할 문제
public class QuestionResponse {
    private String question;
    private List<String> options;
    private String answer;
    private String quizExplain;
    private String questionType;
}
