package com.ohyes.GrowUpMoney.domain.quiz.entity;

import com.ohyes.GrowUpMoney.domain.quiz.entity.enums.Difficulty;
import com.ohyes.GrowUpMoney.domain.quiz.entity.enums.QuestionType;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_question")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="lesson_id", unique = true, nullable = false)
    private Lesson lesson;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    @Column(name="difficulty", nullable = false)
    private Difficulty difficulty;

    @Column(name="stem", nullable = false, columnDefinition = "TEXT")
    private String stem;


    @Type(JsonType.class)
    @Column(name = "options", columnDefinition = "json")
    private List<String> options;


    @Column(name= "answer_key", nullable = false)
    private String answerKey;

    @Column(name="point_reward", nullable = false)
    private Integer pointReward;

    @Column(name="is_premium", nullable=false)
    private Boolean isPremium;

    @Column(name="order_index", nullable = false)
    private Integer orderIndex;

    @Column(name="explanation")
    private String explanation;

    @CreationTimestamp
    private LocalDateTime createdAt;


    @PrePersist
    // entity 처음 저장 직후 createdAt 현재시간으로 저장
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    // 유저가 대답한 답이 맞는지 확인함
    public boolean checkAnswer(String userAnswer){
        if (userAnswer==null) return false;
        return this.answerKey.trim().equalsIgnoreCase(userAnswer.trim());
    }

    // 플랜이 일반이면 주는 포인트 100, 프리미엄이면 500
    public void updateDifficulty(Difficulty newDifficulty){
        switch(newDifficulty){
            case NORMAL -> pointReward = 100;
            case PREMIUM -> pointReward = 500;
        }
    }

}
