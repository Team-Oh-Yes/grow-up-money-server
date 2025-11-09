package com.ohyes.GrowUpMoney.domain.roadmap.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_theme")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoadmapThemeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private Integer orderIndex;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

}
