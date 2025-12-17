package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 作业提交表
 */
@Data
@Entity
@Table(name = "assignment_submissions")
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId; // 作业ID

    @Column(name = "student_id", nullable = false)
    private Long studentId; // 学生ID

    @Column(name = "answer_records", columnDefinition = "TEXT")
    private String answerRecords; // 答题记录ID列表（JSON数组）

    @Column(name = "total_score")
    private Double totalScore = 0.0; // 总分

    @Column(name = "auto_score")
    private Double autoScore = 0.0; // 自动评分

    @Column(name = "manual_score")
    private Double manualScore; // 人工评分

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.SUBMITTED; // 提交状态

    @Column(name = "is_late")
    private Boolean isLate = false; // 是否迟交

    @Column(name = "time_spent")
    private Integer timeSpent; // 用时（分钟）

    @Column(name = "submit_time")
    private LocalDateTime submitTime = LocalDateTime.now(); // 提交时间

    @Column(name = "graded_time")
    private LocalDateTime gradedTime; // 批改时间

    @Column(name = "graded_by")
    private Long gradedBy; // 批改人ID

    @Column(columnDefinition = "TEXT")
    private String feedback; // 教师评语

    public enum SubmissionStatus {
        SUBMITTED,  // 已提交
        GRADING,    // 批改中
        GRADED,     // 已批改
        RETURNED    // 已返还
    }
}
