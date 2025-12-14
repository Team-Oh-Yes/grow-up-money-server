package com.ohyes.GrowUpMoney.domain.quiz.service;

import com.ohyes.GrowUpMoney.domain.quiz.dto.request.QuestionCreateRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.request.QuestionUpdateRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.response.QuestionResponse;
import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import com.ohyes.GrowUpMoney.domain.quiz.exception.QuizException;
import com.ohyes.GrowUpMoney.domain.quiz.repository.QuestionRepository;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import com.ohyes.GrowUpMoney.domain.roadmap.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuizAdminService {

    private final QuestionRepository questionRepository;
    private final LessonRepository lessonRepository;

    @Transactional
    public QuestionResponse createQuestion(QuestionCreateRequest request) {
        log.info("문제 생성: lessonId={}, type={}", request.getLessonId(), request.getType());

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new QuizException.LessonNotFoundException(request.getLessonId()));

        Integer pointReward = request.getPointReward();
        if (pointReward == null) {
            pointReward = request.getDifficulty().getPointReward();
        }

        Question question = Question.builder()
                .lesson(lesson)
                .type(request.getType())
                .difficulty(request.getDifficulty())
                .stem(request.getStem())
                .options(request.getOptions())
                .answerKey(request.getAnswerKey())
                .pointReward(pointReward)
                .isPremium(request.getIsPremium())
                .orderIndex(request.getOrderIndex())
                .explanation(request.getExplanation())
                .build();

        Question saved = questionRepository.save(question);
        log.info("문제 생성 완료: questionId={}", saved.getId());

        return QuestionResponse.from(saved);
    }

    @Transactional
    public QuestionResponse updateQuestion(Long questionId, QuestionUpdateRequest request) {
        log.info("문제 수정: questionId={}", questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuizException.QuestionNotFoundException(questionId));

        if (request.getDifficulty() != null && request.getDifficulty() != question.getDifficulty()) {
            question.updateDifficulty(request.getDifficulty());
        }

        question.updateContent(request.getStem(), request.getOptions(), request.getAnswerKey(), request.getExplanation());
        question.updateOrderIndex(request.getOrderIndex());

        Question saved = questionRepository.save(question);
        log.info("문제 수정 완료: questionId={}", saved.getId());

        return QuestionResponse.from(saved);
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        log.info("문제 삭제: questionId={}", questionId);

        if (!questionRepository.existsById(questionId)) {
            throw new QuizException.QuestionNotFoundException(questionId);
        }

        questionRepository.deleteById(questionId);
        log.info("문제 삭제 완료: questionId={}", questionId);
    }

    public QuestionResponse getQuestion(Long questionId) {
        log.info("문제 상세 조회: questionId={}", questionId);

        Question question = questionRepository.findByIdWithLessonAndTheme(questionId);
        if (question == null) {
            throw new QuizException.QuestionNotFoundException(questionId);
        }

        return QuestionResponse.from(question);
    }

    public List<QuestionResponse> getQuestionsByLesson(Long lessonId) {
        log.info("단원별 문제 목록 조회: lessonId={}", lessonId);

        List<Question> questions = questionRepository.findByLessonIdWithLesson(lessonId);

        return questions.stream().map(QuestionResponse::simple).collect(Collectors.toList());
    }

    public List<QuestionResponse> getQuestionsByTheme(Long themeId) {
        log.info("테마별 문제 목록 조회: themeId={}", themeId);

        List<Question> questions = questionRepository.findByThemeId(themeId);

        return questions.stream().map(QuestionResponse::from).collect(Collectors.toList());
    }

    public long getQuestionCountByLesson(Long lessonId) {
        return questionRepository.countByLessonId(lessonId);
    }

    public long getQuestionCountByTheme(Long themeId) {
        return questionRepository.countByThemeId(themeId);
    }
}