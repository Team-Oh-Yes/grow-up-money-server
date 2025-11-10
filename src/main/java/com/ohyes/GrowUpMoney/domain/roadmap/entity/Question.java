package com.ohyes.GrowUpMoney.domain.roadmap.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // lesson: question, 1: N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String difficulty;

    @Column(nullable = false)
    @Lob
    private String stem;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> options;

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
