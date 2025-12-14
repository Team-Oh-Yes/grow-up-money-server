package com.ohyes.GrowUpMoney.domain.quiz.dto.response;

import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
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

// 해당 문제
public class QuestionResponse {

    private Long questionId;
    private Long lessonId;
    private String lessonTitle;
    private Long themeId;
    private String themeTitle;
    private QuestionType type;
    private Difficulty difficulty;
    private String stem;
    private List<String> options;
    private Integer pointReward;
    private Boolean isPremium;
    private Integer orderIndex;


    public static QuestionResponse from(Question question) {
        return QuestionResponse.builder()
                .questionId(question.getId())
                .lessonId(question.getLesson().getId())
                .lessonTitle(question.getLesson().getTitle())
                .themeId(question.getLesson().getTheme().getId())
                .themeTitle(question.getLesson().getTheme().getTitle())
                .type(question.getType())
                .difficulty(question.getDifficulty())
                .stem(question.getStem())
                .options(question.getOptions())
                .pointReward(question.getPointReward())
                .isPremium(question.getIsPremium())
                .orderIndex(question.getOrderIndex())
                .build();
    }

    public static QuestionResponse simple(Question question) {
        return QuestionResponse.builder()
                .questionId(question.getId())
                .lessonId(question.getLesson().getId())
                .type(question.getType())
                .difficulty(question.getDifficulty())
                .stem(question.getStem())
                .options(question.getOptions())
                .pointReward(question.getPointReward())
                .isPremium(question.getIsPremium())
                .orderIndex(question.getOrderIndex())
                .build();
    }
}
