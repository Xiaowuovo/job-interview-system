package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "answer_records")
public class AnswerRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer; // 用户答案

    @Column(name = "is_correct")
    private Boolean isCorrect;

    private Double score; // 得分

    @Column(name = "time_spent")
    private Integer timeSpent; // 用时（秒）

    @Column(name = "submit_count")
    private Integer submitCount = 1; // 提交次数

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
