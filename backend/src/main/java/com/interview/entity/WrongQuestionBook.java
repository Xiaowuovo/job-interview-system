package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "wrong_question_book")
public class WrongQuestionBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "wrong_count")
    private Integer wrongCount = 1; // 错误次数

    @Column(name = "review_count")
    private Integer reviewCount = 0; // 复习次数

    @Column(name = "last_review_at")
    private LocalDateTime lastReviewAt;

    @Column(name = "next_review_at")
    private LocalDateTime nextReviewAt;

    @Column(name = "is_mastered")
    private Boolean isMastered = false; // 是否已掌握

    @Column(columnDefinition = "TEXT")
    private String notes; // 笔记

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
