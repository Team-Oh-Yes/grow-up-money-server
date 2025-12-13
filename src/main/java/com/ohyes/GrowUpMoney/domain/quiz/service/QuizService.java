package com.ohyes.GrowUpMoney.domain.quiz.service;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
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


     //특정 Lesson의 퀴즈 문제 목록 조회

    public QuizListResponse getQuizList(Long lessonId) {
        List<Question> questions = questionRepository.findByLessonIdWithLesson(lessonId);

        if (questions.isEmpty()) {
            throw new EntityNotFoundException("해당 단원에 문제가 없습니다.");
        }

        String lessonName = questions.get(0).getLesson().getTitle();

        return QuizListResponse.from(lessonId, lessonName, questions);
    }

    //퀴즈 시작 전 하트 체크
    public void checkHeartsBeforeQuiz(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 하트 리셋이 필요한 경우 자동 리셋
        if (member.needsHeartReset()) {
            member.resetHearts();
            memberRepository.save(member);
        }

        if (member.getHearts() <= 0) {
            throw new IllegalStateException("하트가 부족합니다. 하트를 구매해주세요.");
        }
    }


     // 퀴즈 답안 제출 및 채점
    @Transactional
    public AnswerResultResponse submitAnswer(Long memberId, AnswerSubmitRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("문제를 찾을 수 없습니다."));

        // 정답 체크
        boolean isCorrect = question.checkAnswer(request.getUserAnswer());

        // 틀렸으면 하트 차감
        if (!isCorrect) {
            member.deductHeart();
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
        memberRepository.save(member);

        return AnswerResultResponse.builder()
                .questionId(question.getId())
                .isCorrect(isCorrect)
                .correctAnswer(isCorrect ? null : question.getAnswerKey())
                .explanation(question.getExplanation())
                .remainingHearts(member.getHearts())
                .build();
    }


     // 틀린 문제 목록 조회
    public WrongAnswerListResponse getWrongAnswers(Long memberId, Long lessonId) {
        List<QuizAttempt> wrongAttempts;

        if (lessonId != null) {
            wrongAttempts = quizAttemptRepository
                    .findWrongAttemptsByMemberAndLesson(memberId, lessonId);
        } else {
            wrongAttempts = quizAttemptRepository
                    .findWrongAttemptsByMemberId(memberId);
        }

        List<WrongAnswerListResponse.WrongAnswerDetail> details = wrongAttempts.stream()
                .map(attempt -> WrongAnswerListResponse.WrongAnswerDetail.builder()
                        .questionId(attempt.getQuestion().getId())
                        .content(attempt.getQuestion().getStem())
                        .userAnswer(attempt.getUserAnswer())
                        .correctAnswer(attempt.getQuestion().getAnswerKey())
                        .build())
                .collect(Collectors.toList());

        return WrongAnswerListResponse.builder()
                .wrongAnswers(details)
                .build();
    }

    /**
     * Lesson별 진행도 조회
     */
    public LessonQuizSummaryResponse getLessonProgress(Long memberId, Long lessonId) {
        long totalQuestions = questionRepository.countByLessonId(lessonId);
        long correctCount = quizAttemptRepository
                .countCorrectByMemberAndLesson(memberId, lessonId);

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

     // 현재 하트 조회
    public HeartResponse getCurrentHearts(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 하트 리셋이 필요한 경우 자동 리셋
        if (member.needsHeartReset()) {
            member.resetHearts();
            memberRepository.save(member);
        }

        return HeartResponse.builder()
                .hearts(member.getHearts())
                .build();
    }


    // 하트 구매

    @Transactional
    public HeartPurchaseResponse purchaseHearts(Long memberId, int amount) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if (amount <= 0) {
            throw new IllegalArgumentException("구매 개수는 1개 이상이어야 합니다.");
        }

        // 최대 5개까지만 보유 가능
        int currentHearts = member.getHearts();
        if (currentHearts >= 5) {
            throw new IllegalStateException("이미 하트가 최대치입니다.");
        }

        int availableSpace = 5 - currentHearts;
        int actualPurchased = Math.min(amount, availableSpace);

        // 하트 구매 (5포인트 * 개수)
        int cost = actualPurchased * 5;
        if (member.getPointBalance() < cost) {
            throw new IllegalStateException("포인트가 부족합니다. 필요: " + cost);
        }

        for (int i = 0; i < actualPurchased; i++) {
            member.purchaseHeart();
        }

        memberRepository.save(member);

        return HeartPurchaseResponse.builder()
                .purchased(actualPurchased)
                .totalHearts(member.getHearts())
                .build();
    }
}