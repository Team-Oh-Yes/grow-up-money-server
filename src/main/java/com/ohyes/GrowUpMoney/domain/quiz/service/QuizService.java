package com.ohyes.GrowUpMoney.domain.quiz.service;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.auth.service.HeartService;
import com.ohyes.GrowUpMoney.domain.quiz.dto.request.AnswerSubmitRequest;
import com.ohyes.GrowUpMoney.domain.quiz.dto.response.*;
import com.ohyes.GrowUpMoney.domain.quiz.entity.Question;
import com.ohyes.GrowUpMoney.domain.quiz.entity.QuizAttempt;
import com.ohyes.GrowUpMoney.domain.quiz.repository.QuestionRepository;
import com.ohyes.GrowUpMoney.domain.quiz.repository.QuizAttemptRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final MemberRepository memberRepository;
    private final HeartService heartService;

    // 특정 Lesson의 퀴즈 문제 목록 조회
    public QuizListResponse getQuizList(Long lessonId) {
        List<Question> questions = questionRepository.findByLessonIdWithLesson(lessonId);

        if (questions.isEmpty()) {
            throw new EntityNotFoundException("해당 단원에 문제가 없습니다.");
        }

        String lessonName = questions.get(0).getLesson().getTitle();

        return QuizListResponse.from(lessonId, lessonName, questions);
    }

    // 퀴즈 시작 전 하트 체크
    public void checkHeartsBeforeQuiz(String username) {
        // HeartService에서 하트 리셋 체크
        heartService.checkAndResetHearts(username);

        // 하트 개수 확인
        if (!heartService.hasEnoughHearts(username)) {
            throw new IllegalStateException("하트가 부족합니다. 하트를 구매해주세요.");
        }
    }

    // 퀴즈 답안 제출 및 채점
    @Transactional
    public AnswerResultResponse submitAnswer(String username, AnswerSubmitRequest request) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("문제를 찾을 수 없습니다."));

        // 정답 체크
        boolean isCorrect = question.checkAnswer(request.getUserAnswer());

        // 틀렸으면 하트 차감 (HeartService 사용)
        if (!isCorrect) {
            heartService.deductHeart(username);
        }

        // 시도 기록 저장 (포인트는 나중에 단원 완료 시 일괄 지급)
        QuizAttempt attempt = QuizAttempt.builder()
                .member(member)
                .question(question)
                .userAnswer(request.getUserAnswer())
                .isCorrect(isCorrect)
                .pointsEarned(0)  // 개별 문제에선 0
                .build();

        quizAttemptRepository.save(attempt);

        // 현재 하트 개수 조회
        int remainingHearts = heartService.getCurrentHearts(username);

        return AnswerResultResponse.builder()
                .questionId(question.getId())
                .isCorrect(isCorrect)
                .correctAnswer(isCorrect ? null : question.getAnswerKey())
                .explanation(question.getExplanation())
                .remainingHearts(remainingHearts)
                .build();
    }

    // 틀린 문제 목록 조회
    public WrongAnswerListResponse getWrongAnswers(String username, Long lessonId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        List<QuizAttempt> wrongAttempts;

        if (lessonId != null) {
            wrongAttempts = quizAttemptRepository
                    .findWrongAttemptsByMemberAndLesson(member.getId(), lessonId);
        } else {
            wrongAttempts = quizAttemptRepository
                    .findWrongAttemptsByMemberId(member.getId());
        }

        List<WrongAnswerListResponse.WrongAnswerDetail> details = wrongAttempts.stream()
                .map(attempt -> WrongAnswerListResponse.WrongAnswerDetail.builder()
                        .questionId(attempt.getQuestion().getId())
                        .stem(attempt.getQuestion().getStem())
                        .userAnswer(attempt.getUserAnswer())
                        .correctAnswer(attempt.getQuestion().getAnswerKey())
                        .build())
                .collect(Collectors.toList());

        return WrongAnswerListResponse.builder()
                .wrongAnswers(details)
                .build();
    }

    // Lesson별 진행도 조회
    public LessonQuizSummaryResponse getLessonProgress(String username, Long lessonId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        long totalQuestions = questionRepository.countByLessonId(lessonId);
        long correctCount = quizAttemptRepository
                .countCorrectByMemberAndLesson(member.getId(), lessonId);

        // Lesson 정보 조회
        List<Question> questions = questionRepository.findByLessonIdWithLesson(lessonId);
        String lessonName = questions.isEmpty() ? "Unknown" :
                questions.get(0).getLesson().getTitle();

        return LessonQuizSummaryResponse.of(
                lessonId,
                lessonName,
                (int) totalQuestions,
                (int) correctCount
        );
    }
}