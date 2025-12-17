package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "test_records")
public class TestRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String category;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "correct_count")
    private Integer correctCount;

    private Double score;

    @Column(name = "time_spent")
    private Integer timeSpent; // 秒

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
