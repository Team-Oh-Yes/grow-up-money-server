package com.ohyes.GrowUpMoney.domain.quiz.repository;

import com.ohyes.GrowUpMoney.domain.quiz.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
}
