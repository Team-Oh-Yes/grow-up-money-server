package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder

// 틀린 문제 목록
public class WrongAnswerListResponse {
    private List<WrongAnswerDetail> wrongAnswers;

    @Getter
    @Builder
    public static class WrongAnswerDetail {
        private Long questionId;
        private String content;          // 문제 내용
        private String userAnswer;       // 사용자가 틀린 답
        private String correctAnswer;    // 실제 정답
    }
}
