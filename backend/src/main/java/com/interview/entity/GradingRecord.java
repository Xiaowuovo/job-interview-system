package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 批改记录表
 */
@Data
@Entity
@Table(name = "grading_records")
public class GradingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id", nullable = false)
    private Long submissionId; // 作业提交ID

    @Column(name = "question_id", nullable = false)
    private Long questionId; // 题目ID

    @Column(name = "grader_id", nullable = false)
    private Long graderId; // 批改人ID

    @Column(name = "score")
    private Double score; // 得分

    @Column(name = "max_score")
    private Double maxScore; // 满分

    @Column(columnDefinition = "TEXT")
    private String comments; // 批改评语

    @Column(name = "is_correct")
    private Boolean isCorrect; // 是否正确

    @Column(columnDefinition = "TEXT")
    private String highlights; // 高亮批注（JSON格式）

    @Column(name = "graded_at")
    private LocalDateTime gradedAt = LocalDateTime.now(); // 批改时间

    @Column(name = "time_spent")
    private Integer timeSpent; // 批改用时（秒）
}
