package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "interview_sessions")
public class InterviewSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType; // TECHNICAL, BEHAVIORAL, HR

    @Column(length = 100)
    private String position; // 应聘岗位

    @Column(name = "resume_id")
    private Long resumeId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ONGOING; // ONGOING, COMPLETED, ABANDONED

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private Integer duration; // 时长（秒）

    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "avg_score")
    private Double avgScore; // 平均得分

    @Column(columnDefinition = "LONGTEXT")
    private String conversation; // AI对话记录（JSON格式）

    @Column(columnDefinition = "TEXT")
    private String feedback; // AI反馈

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // 枚举类型定义
    public enum SessionType {
        TECHNICAL, // 技术面试
        BEHAVIORAL, // 行为面试
        HR // HR面试
    }

    public enum Status {
        ONGOING,
        COMPLETED,
        ABANDONED
    }
}
