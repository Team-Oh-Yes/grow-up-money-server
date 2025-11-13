package com.ohyes.GrowUpMoney.domain.roadmap.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_theme")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer orderIndex;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    @Builder
    public Theme(String title, Integer orderIndex) {
        this.title = title;
        this.orderIndex = orderIndex;
    }

    public void updatetitle(String title) {
        this.title = title;
    }

    public void updateOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void addLesson(Lesson lesson) {
        this.lessons.add(lesson);
        lesson.setTheme(this);
    }

}
