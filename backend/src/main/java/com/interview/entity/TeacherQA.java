package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 教师答疑表
 */
@Data
@Entity
@Table(name = "teacher_qa")
public class TeacherQA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId; // 学生ID

    @Column(name = "teacher_id")
    private Long teacherId; // 教师ID

    @Column(name = "class_id")
    private Long classId; // 班级ID

    @Column(nullable = false, length = 200)
    private String question; // 问题标题

    @Column(columnDefinition = "TEXT")
    private String questionDetail; // 问题详情

    @Column(columnDefinition = "TEXT")
    private String answer; // 回答内容

    @Enumerated(EnumType.STRING)
    private QAStatus status = QAStatus.PENDING; // 状态

    @Column(name = "is_public")
    private Boolean isPublic = true; // 是否公开

    @Column(name = "like_count")
    private Integer likeCount = 0; // 点赞数

    @Column(name = "asked_at")
    private LocalDateTime askedAt = LocalDateTime.now(); // 提问时间

    @Column(name = "answered_at")
    private LocalDateTime answeredAt; // 回答时间

    public enum QAStatus {
        PENDING,   // 待回答
        ANSWERED,  // 已回答
        CLOSED     // 已关闭
    }
}
