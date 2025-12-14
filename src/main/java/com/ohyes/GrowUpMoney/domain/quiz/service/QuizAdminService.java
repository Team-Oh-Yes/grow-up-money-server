package com.ohyes.GrowUpMoney.domain.quiz.service;

import com.ohyes.GrowUpMoney.domain.quiz.dto.request.QuestionCreateRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.request.QuestionUpdateRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.response.QuestionResponse;
import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import com.ohyes.GrowUpMoney.domain.quiz.enums.Difficulty;
import com.ohyes.GrowUpMoney.domain.quiz.repository.QuestionRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.LessonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizAdminService {

    private final QuestionRepository questionRepository;
    private final LessonRepository lessonRepository;

   // 문제 생성
    public QuestionResponse createQuestion(QuestionCreateRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson을 찾을 수 없습니다."));

        // orderIndex 자동 설정 (마지막 + 1)
        long currentCount = questionRepository.countByLessonId(request.getLessonId());

        Difficulty difficulty = Difficulty.valueOf(request.getDifficulty());

        Question question = Question.builder()
                .lesson(lesson)
                .difficulty(difficulty)
                .stem(request.getQuestion())
                .options(request.getOptions())
                .answerKey(request.getAnswer())
                .explanation(request.getQuizExplain())
                .orderIndex((int) (currentCount + 1))
                .isPremium(difficulty == Difficulty.PREMIUM)
                .pointReward(difficulty == Difficulty.PREMIUM ? 500 : 100)
                .build();

        Question saved = questionRepository.save(question);

        return QuestionResponse.from(saved, true);
    }

    // 문제 수정
    public QuestionResponse updateQuestion(Long questionId, QuestionUpdateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("문제를 찾을 수 없습니다."));

        // Lesson 변경 시
        if (request.getLessonId() != null &&
                !question.getLesson().getId().equals(request.getLessonId())) {
            Lesson newLesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new EntityNotFoundException("Lesson을 찾을 수 없습니다."));
            question.setLesson(newLesson);
        }

        // 필드 업데이트
        if (request.getQuestion() != null) {
            question.setStem(request.getQuestion());
        }
        if (request.getOptions() != null) {
            question.setOptions(request.getOptions());
        }
        if (request.getAnswer() != null) {
            question.setAnswerKey(request.getAnswer());
        }
        if (request.getQuizExplain() != null) {
            question.setExplanation(request.getQuizExplain());
        }
        if (request.getDifficulty() != null) {
            Difficulty newDifficulty = Difficulty.valueOf(request.getDifficulty());
            question.setDifficulty(newDifficulty);
            question.updateDifficulty(newDifficulty);
        }

        Question updated = questionRepository.save(question);

        return QuestionResponse.from(updated, true);
    }

    // 문제 삭제
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new EntityNotFoundException("문제를 찾을 수 없습니다.");
        }
        questionRepository.deleteById(questionId);
    }

    // 문제 상세 조회 (관리자용 - 정답 포함)
    @Transactional(readOnly = true)
    public QuestionResponse getQuestionDetail(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("문제를 찾을 수 없습니다."));

        return QuestionResponse.from(question, true);
    }
}
