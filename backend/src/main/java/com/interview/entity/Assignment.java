package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 作业任务表
 */
@Data
@Entity
@Table(name = "assignments")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id", nullable = false)
    private Long classId; // 班级ID

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId; // 教师ID

    @Column(nullable = false, length = 100)
    private String title; // 作业标题

    @Column(columnDefinition = "TEXT")
    private String description; // 作业描述

    @Enumerated(EnumType.STRING)
    private AssignmentType type = AssignmentType.PRACTICE; // 作业类型

    @Column(name = "question_ids", columnDefinition = "TEXT")
    private String questionIds; // 题目ID列表（JSON数组）

    @Column(name = "total_score")
    private Integer totalScore = 100; // 总分

    @Column(name = "pass_score")
    private Integer passScore = 60; // 及格分

    @Column(name = "time_limit")
    private Integer timeLimit; // 时间限制（分钟）

    @Column(name = "start_time")
    private LocalDateTime startTime; // 开始时间

    @Column(name = "deadline")
    private LocalDateTime deadline; // 截止时间

    @Column(name = "allow_late_submission")
    private Boolean allowLateSubmission = false; // 是否允许迟交

    @Column(name = "is_published")
    private Boolean isPublished = false; // 是否已发布

    @Column(name = "submission_count")
    private Integer submissionCount = 0; // 提交人数

    @Column(name = "avg_score")
    private Double avgScore = 0.0; // 平均分

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum AssignmentType {
        PRACTICE,   // 练习
        HOMEWORK,   // 作业
        EXAM        // 考试
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
