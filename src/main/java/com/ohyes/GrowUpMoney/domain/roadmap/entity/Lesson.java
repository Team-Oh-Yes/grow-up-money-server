package com.ohyes.GrowUpMoney.domain.roadmap.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_lesson")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long id;

    // Theme: lesson, 1: N
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer orderIndex;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Lesson(Theme theme, String title, Integer orderIndex) {
        this.theme = theme;
        this.title = title;
        this.orderIndex = orderIndex;
    }

    public void updatetitle(String title) {
        this.title = title;
    }

    public void updateOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    protected void setTheme(Theme theme) {
        this.theme = theme;
    }

}
