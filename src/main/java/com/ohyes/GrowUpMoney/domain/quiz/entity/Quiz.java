package com.ohyes.GrowUpMoney.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_question")
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", unique = true, nullable = false)
    private Long id;


    @JoinColumn(name="lesson_id", unique = true, nullable = false)
    private Long lesson;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String difficulty;

    @Lob // Long object
    @Column(nullable = false, columnDefinition = "TEXT")
    private String stem;


    @Column(nullable = false)
    private String options;

    @Column(name= "answer_key", nullable = false)
    private String answer;

    @Column(name="point_reward", nullable = false)
    private int pointReward;

    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium;

    @CreationTimestamp
    private LocalDateTime createAt;


//    변경될거같은거

}
