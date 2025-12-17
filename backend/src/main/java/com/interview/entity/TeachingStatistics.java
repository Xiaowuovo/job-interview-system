package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教学统计表
 */
@Data
@Entity
@Table(name = "teaching_statistics")
public class TeachingStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId; // 教师ID

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate; // 统计日期

    @Enumerated(EnumType.STRING)
    @Column(name = "stat_type")
    private StatType statType = StatType.DAILY; // 统计类型

    @Column(name = "student_count")
    private Integer studentCount = 0; // 学生数

    @Column(name = "active_students")
    private Integer activeStudents = 0; // 活跃学生数

    @Column(name = "new_students")
    private Integer newStudents = 0; // 新增学生数

    @Column(name = "assignment_count")
    private Integer assignmentCount = 0; // 发布作业数

    @Column(name = "submission_count")
    private Integer submissionCount = 0; // 提交作业数

    @Column(name = "avg_score")
    private Double avgScore = 0.0; // 平均分

    @Column(name = "graded_count")
    private Integer gradedCount = 0; // 批改数量

    @Column(name = "content_created")
    private Integer contentCreated = 0; // 创建内容数

    @Column(name = "total_teaching_time")
    private Integer totalTeachingTime = 0; // 总教学时长（分钟）

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum StatType {
        DAILY,   // 日统计
        WEEKLY,  // 周统计
        MONTHLY  // 月统计
    }
}
