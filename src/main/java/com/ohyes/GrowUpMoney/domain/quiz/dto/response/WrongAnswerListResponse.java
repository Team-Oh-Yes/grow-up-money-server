package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 틀린 문제 목록
public class WrongAnswerListResponse {

    private Integer totalCount;
    private Long lessonId;
    private String lessonTitle;
    private List<QuestionResponse> wrongQuestions;

    public static WrongAnswerListResponse ofLesson(Long lessonId, String lessonTitle, List<QuestionResponse> wrongQuestions) {
        return WrongAnswerListResponse.builder()
                .totalCount(wrongQuestions.size())
                .lessonId(lessonId)
                .lessonTitle(lessonTitle)
                .wrongQuestions(wrongQuestions)
                .build();
    }

    public static WrongAnswerListResponse ofAll(List<QuestionResponse> wrongQuestions) {
        return WrongAnswerListResponse.builder()
                .totalCount(wrongQuestions.size())
                .wrongQuestions(wrongQuestions)
                .build();
    }
}
