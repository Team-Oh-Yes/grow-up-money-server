package com.ohyes.GrowUpMoney.domain.roadmap.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_lesson")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Theme: lesson, 1: N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String title;

    @Column(nullable = false)
    private Integer orderIndex;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

}
