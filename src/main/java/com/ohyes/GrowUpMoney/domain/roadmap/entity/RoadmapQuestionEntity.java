package com.ohyes.GrowUpMoney.domain.roadmap.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.w3c.dom.Text;

import java.time.LocalDateTime;

@Entity
public class RoadmapQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // lesson: question, 1: N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private RoadmapLessonEntity lesson;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String difficulty;

    @Column(nullable = false)
    @Lob
    private String stem;

    // json형식으로 객관식 만들어야함 다른 Entity로 뺄 생각

    //정답
    @Column(nullable = false, length = 255)
    private String answerKey;

    @Column(nullable = false)
    private Integer point_reward;

    @Column(nullable = false)
    private Boolean is_premium = false;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime created_at;

}
