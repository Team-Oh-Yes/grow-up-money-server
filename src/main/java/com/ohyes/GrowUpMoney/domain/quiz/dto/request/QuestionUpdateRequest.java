package com.ohyes.GrowUpMoney.domain.quiz.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor

// 관리자가 기존 문제 수정할 때
public class QuestionUpdateRequest {
    private String question;
    private List<String> options;
    private String answer;
    private String quizExplain;
    private String difficulty;
    private Long lessonId;
}
