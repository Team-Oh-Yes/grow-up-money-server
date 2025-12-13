
package com.ohyes.GrowUpMoney.domain.quiz.service;

import com.ohyes.GrowUpMoney.domain.auth.entity.Member;
import com.ohyes.GrowUpMoney.domain.auth.repository.MemberRepository;
import com.ohyes.GrowUpMoney.domain.quiz.enums.QuizRewardConstants;
import com.ohyes.GrowUpMoney.domain.quiz.repository.QuestionRepository;
import com.ohyes.GrowUpMoney.domain.quiz.repository.QuizAttemptRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
// 보상지급
public class QuizRewardService {

    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    // Lesson 완료 보상 지급
    public void rewardLessonClear(Long memberId, Long lessonId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // Lesson의 모든 문제를 맞췄는지 확인
        long totalQuestions = questionRepository.countByLessonId(lessonId);
        long correctCount = quizAttemptRepository
                .countCorrectByMemberAndLesson(memberId, lessonId);

        if (correctCount >= totalQuestions && totalQuestions > 0) {
            member.addBoundPoint(QuizRewardConstants.LESSON_CLEAR.getReward());
            memberRepository.save(member);
        }
    }
}