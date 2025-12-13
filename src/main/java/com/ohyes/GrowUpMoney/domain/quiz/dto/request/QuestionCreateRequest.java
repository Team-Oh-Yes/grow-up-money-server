package com.ohyes.GrowUpMoney.domain.quiz.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor

// 관리자가 새 문제 만들때
public class QuestionCreateRequest {
    private String question;
    private List<String> options;
    private String answer;
    private String quizExplain;
    private String difficulty;
    private Long lessonId;

}

