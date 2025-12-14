package com.ohyes.GrowUpMoney.domain.quiz.entity;

import com.ohyes.GrowUpMoney.domain.quiz.enums.Difficulty;
import com.ohyes.GrowUpMoney.domain.quiz.enums.QuestionType;
import com.ohyes.GrowUpMoney.domain.roadmap.entity.Lesson;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import java.time.LocalDateTime;
import java.util.List;

// 퀴즈 내용 담긴거 테이블
@Entity
@Table(name = "tb_question")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
public class Question {

    // 퀴즈 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", unique = true, nullable = false)
    private Long id;

    // 레슨했던 아이디 들고오기
    @ManyToOne
    @JoinColumn(name="lesson_id", nullable = false)
    private Lesson lesson;

    // 주관식인지 객관식인지 <- 지금은이거안씀
/*    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private QuestionType type;*/


    // 일반인지 프리미엄인지
    @Enumerated(EnumType.STRING)
    @Column(name="difficulty", nullable = false)
    private Difficulty difficulty;


    //이게뭐지
    @Column(name="stem", nullable = false, columnDefinition = "TEXT")
    private String stem;

    // 객관식일 경우 뭔 선택지인지
    @Type(JsonType.class)
    @Column(name = "options", columnDefinition = "json")
    private List<String> options;


    @Column(name= "answer_key", nullable = false)
    private String answerKey;

    // 퀴즈 다 정답일시 주는 보상포인트
    @Column(name="point_reward", nullable = false)
    private Integer pointReward;

    // difficulty가 프리미엄이면 is_premium true임
    @Column(name="is_premium", nullable=false)
    private Boolean isPremium;

    // 답변
    @Column(name="order_index", nullable = false)
    private Integer orderIndex;

    // 퀴즈의 문제풀이, 해석
    @Column(name="explanation")
    private String explanation;

    // 퀴즈
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
