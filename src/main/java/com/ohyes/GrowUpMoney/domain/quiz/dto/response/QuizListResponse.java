package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder

// 특정 단원/테마에서 문제 목록 보여줌
public class QuizListResponse {
    private Long lessonId;                    // 단원 ID
    private String lessonName;                // 단원 이름
    private List<QuestionResponse> questions; // 문제 목록
}