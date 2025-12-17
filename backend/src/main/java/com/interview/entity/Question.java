package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 基本信息
    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type; // CHOICE, MULTIPLE_CHOICE, CODING, SHORT_ANSWER, SYSTEM_DESIGN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty; // EASY, MEDIUM, HARD

    // 分类标签
    private String category; // 一级分类: Java、前端、算法、数据库等
    @Column(name = "sub_category")
    private String subCategory; // 二级分类

    private String tags; // 标签（逗号分隔）

    // 选择题字段
    @Column(name = "option_a", columnDefinition = "TEXT")
    private String optionA;

    @Column(name = "option_b", columnDefinition = "TEXT")
    private String optionB;

    @Column(name = "option_c", columnDefinition = "TEXT")
    private String optionC;

    @Column(name = "option_d", columnDefinition = "TEXT")
    private String optionD;

    @Column(name = "correct_answer")
    private String correctAnswer; // A, B, C, D 或多选 AB,CD

    // 编程题字段
    @Column(name = "input_format", columnDefinition = "TEXT")
    private String inputFormat;

    @Column(name = "output_format", columnDefinition = "TEXT")
    private String outputFormat;

    @Column(name = "sample_input", columnDefinition = "TEXT")
    private String sampleInput;

    @Column(name = "sample_output", columnDefinition = "TEXT")
    private String sampleOutput;

    @Column(name = "time_limit")
    private Integer timeLimit; // 时间限制（ms）

    @Column(name = "memory_limit")
    private Integer memoryLimit; // 内存限制（MB）

    // 答案与解析
    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "reference_answer", columnDefinition = "TEXT")
    private String referenceAnswer;

    @Column(name = "key_points", length = 500)
    private String keyPoints; // 考察知识点

    // 统计数据
    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    @Column(name = "pass_count")
    private Integer passCount = 0;

    @Column(name = "pass_rate")
    private Double passRate = 0.0;

    @Column(name = "avg_score")
    private Double avgScore = 0.0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "collect_count")
    private Integer collectCount = 0;

    // 元数据
    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 枚举类型定义
    public enum QuestionType {
        CHOICE, // 单选题
        MULTIPLE_CHOICE, // 多选题
        CODING, // 编程题
        SHORT_ANSWER, // 简答题
        SYSTEM_DESIGN // 系统设计题
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
